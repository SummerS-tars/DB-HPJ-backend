package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.request.EvaluationTagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationTagResponse;
import top.thesumst.llm_eval_backend.dto.response.EvaluationStatisticsResponse;
import top.thesumst.llm_eval_backend.entity.EvaluationTag;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.EvaluationTagRepository;
import top.thesumst.llm_eval_backend.repository.EvaluationResultRepository;
import top.thesumst.llm_eval_backend.repository.VersionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for evaluation tag operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationTagService {

    private final EvaluationTagRepository evaluationTagRepository;
    private final EvaluationResultRepository evaluationResultRepository;
    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new evaluation tag
     */
    @Transactional
    public EvaluationTagResponse createEvaluationTag(EvaluationTagCreateRequest request) {
        log.info("Creating evaluation tag for model: {}, version: {}", request.getModel(), request.getDataSetVersion());

        // Validate data set version exists
        if (!versionRepository.existsByVersion(request.getDataSetVersion())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "数据集版本不存在: " + request.getDataSetVersion());
        }

        // Check if evaluation tag already exists for this model and version
        if (evaluationTagRepository.existsByModelAndDataSetVersion(request.getModel(), request.getDataSetVersion())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                    "该模型和数据集版本的评估标签已存在");
        }

        EvaluationTag evaluationTag = modelMapper.map(request, EvaluationTag.class);
        EvaluationTag savedTag = evaluationTagRepository.save(evaluationTag);
        
        log.info("Created evaluation tag: id={}, model={}, version={}", 
                savedTag.getTagId(), savedTag.getModel(), savedTag.getDataSetVersion());
        
        return convertToResponse(savedTag);
    }

    /**
     * Get evaluation tags with pagination and filtering
     */
    public Page<EvaluationTagResponse> getEvaluationTags(int page, int size, String sortBy, String order,
                                                         String model, String dataSetVersion, Integer evaluationTime) {
        
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EvaluationTag> tagPage;
        
        // Handle all combinations of filters
        if (model != null && dataSetVersion != null && evaluationTime != null) {
            tagPage = evaluationTagRepository.findByModelAndDataSetVersionAndEvaluationTime(model, dataSetVersion, evaluationTime, pageable);
        } else if (model != null && dataSetVersion != null) {
            tagPage = evaluationTagRepository.findByModelAndDataSetVersion(model, dataSetVersion, pageable);
        } else if (model != null && evaluationTime != null) {
            tagPage = evaluationTagRepository.findByModelAndEvaluationTime(model, evaluationTime, pageable);
        } else if (dataSetVersion != null && evaluationTime != null) {
            tagPage = evaluationTagRepository.findByDataSetVersionAndEvaluationTime(dataSetVersion, evaluationTime, pageable);
        } else if (model != null) {
            tagPage = evaluationTagRepository.findByModel(model, pageable);
        } else if (dataSetVersion != null) {
            tagPage = evaluationTagRepository.findByDataSetVersion(dataSetVersion, pageable);
        } else if (evaluationTime != null) {
            tagPage = evaluationTagRepository.findByEvaluationTime(evaluationTime, pageable);
        } else {
            tagPage = evaluationTagRepository.findAll(pageable);
        }
        
        return tagPage.map(this::convertToResponse);
    }

    /**
     * Get evaluation tag by ID
     */
    public EvaluationTagResponse getEvaluationTagById(Long id) {
        EvaluationTag tag = evaluationTagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估标签不存在，ID: " + id));
        
        return convertToResponse(tag);
    }

    /**
     * Get latest evaluation tags for each model
     */
    public Page<EvaluationTagResponse> getLatestEvaluationsByModel(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EvaluationTag> tagPage = evaluationTagRepository.findLatestEvaluationsByModel(pageable);
        
        return tagPage.map(this::convertToResponse);
    }

    /**
     * Get all unique models
     */
    public List<String> getAllUniqueModels() {
        return evaluationTagRepository.findAllUniqueModels();
    }

    /**
     * Get all unique data set versions
     */
    public List<String> getAllUniqueDataSetVersions() {
        return evaluationTagRepository.findAllUniqueDataSetVersions();
    }

    /**
     * Get evaluation statistics for a specific tag
     */
    public EvaluationStatisticsResponse getEvaluationStatistics(Long tagId) {
        EvaluationTag tag = evaluationTagRepository.findById(tagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估标签不存在，ID: " + tagId));

        EvaluationStatisticsResponse stats = new EvaluationStatisticsResponse();
        stats.setTagId(tagId);
        stats.setModel(tag.getModel());
        stats.setDataSetVersion(tag.getDataSetVersion());

        // Get basic counts
        stats.setTotalResults(evaluationResultRepository.countByEvaluationTagId(tagId));

        // Get status counts
        List<Object[]> statusCounts = evaluationResultRepository.countByStatusForEvaluationTag(tagId);
        Map<String, Long> statusMap = statusCounts.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        stats.setStatusCounts(statusMap);
        
        // Set individual status counts
        stats.setPendingResults(statusMap.getOrDefault("PENDING", 0L));
        stats.setAnalyzedResults(statusMap.getOrDefault("ANALYZED", 0L));
        stats.setOmittedResults(statusMap.getOrDefault("OMITTED", 0L));

        // Get type counts
        List<Object[]> typeCounts = evaluationResultRepository.countByTypeForEvaluationTag(tagId);
        Map<String, Long> typeMap = typeCounts.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        stats.setTypeCounts(typeMap);
        
        // Set individual type counts
        stats.setObjectiveResults(typeMap.getOrDefault("OBJECTIVE", 0L));
        stats.setSubjectiveResults(typeMap.getOrDefault("SUBJECTIVE", 0L));

        log.info("Retrieved evaluation statistics for tag {}: {} total results", tagId, stats.getTotalResults());
        
        return stats;
    }

    /**
     * Delete evaluation tag
     */
    @Transactional
    public void deleteEvaluationTag(Long id) {
        EvaluationTag tag = evaluationTagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估标签不存在，ID: " + id));

        // Check if there are associated evaluation results
        long resultCount = evaluationResultRepository.countByEvaluationTagId(id);
        if (resultCount > 0) {
            throw new BusinessException(ErrorCode.CONSTRAINT_VIOLATION, 
                    "无法删除评估标签，存在关联的评估结果: " + resultCount + " 条");
        }

        evaluationTagRepository.delete(tag);
        log.info("Deleted evaluation tag: id={}, model={}", id, tag.getModel());
    }

    /**
     * Convert entity to response DTO
     */
    private EvaluationTagResponse convertToResponse(EvaluationTag tag) {
        EvaluationTagResponse response = modelMapper.map(tag, EvaluationTagResponse.class);
        
        // Add result count
        response.setResultCount(evaluationResultRepository.countByEvaluationTagId(tag.getTagId()));
        
        return response;
    }
} 