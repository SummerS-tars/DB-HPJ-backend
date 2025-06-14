package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.request.AnalysisTagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.AnalysisTagResponse;
import top.thesumst.llm_eval_backend.entity.AnalysisTag;
import top.thesumst.llm_eval_backend.entity.EvaluationTag;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.AnalysisTagRepository;
import top.thesumst.llm_eval_backend.repository.EvaluationTagRepository;
import top.thesumst.llm_eval_backend.repository.EvaluationAnalysisRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for AnalysisTag operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnalysisTagService {

    private final AnalysisTagRepository analysisTagRepository;
    private final EvaluationTagRepository evaluationTagRepository;
    private final EvaluationAnalysisRepository evaluationAnalysisRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new analysis tag
     */
    public AnalysisTagResponse createAnalysisTag(AnalysisTagCreateRequest request) {
        log.info("Creating analysis tag for evaluation tag ID: {}, model: {}", 
                request.getEvaluationTagId(), request.getModel());

        // Check if evaluation tag exists
        EvaluationTag evaluationTag = evaluationTagRepository.findById(request.getEvaluationTagId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Evaluation tag not found with ID: " + request.getEvaluationTagId()));

        // Check for duplicate analysis tag (same evaluation tag + model)
        if (analysisTagRepository.existsByEvaluationTagIdAndModel(request.getEvaluationTagId(), request.getModel())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Analysis tag already exists for evaluation tag ID: " + 
                    request.getEvaluationTagId() + " and model: " + request.getModel());
        }

        // Create analysis tag
        AnalysisTag analysisTag = new AnalysisTag();
        analysisTag.setEvaluationTagId(request.getEvaluationTagId());
        analysisTag.setAnalysisTime(request.getAnalysisTime());
        analysisTag.setModel(request.getModel());

        AnalysisTag savedTag = analysisTagRepository.save(analysisTag);
        log.info("Created analysis tag with ID: {}", savedTag.getAnalysisTagId());

        return convertToResponse(savedTag, evaluationTag.getModel(), 0);
    }

    /**
     * Get analysis tag by ID
     */
    @Transactional(readOnly = true)
    public AnalysisTagResponse getAnalysisTagById(Long id) {
        log.info("Getting analysis tag by ID: {}", id);

        AnalysisTag analysisTag = analysisTagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Analysis tag not found with ID: " + id));

        EvaluationTag evaluationTag = evaluationTagRepository.findById(analysisTag.getEvaluationTagId())
                .orElse(null);

        long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(id);

        return convertToResponse(analysisTag, 
                evaluationTag != null ? evaluationTag.getModel() : null, 
                (int) analysisCount);
    }

    /**
     * Get all analysis tags with pagination
     */
    @Transactional(readOnly = true)
    public Page<AnalysisTagResponse> getAllAnalysisTags(Pageable pageable) {
        log.info("Getting all analysis tags with pagination");

        Page<AnalysisTag> analysisTagPage = analysisTagRepository.findAll(pageable);
        
        return analysisTagPage.map(analysisTag -> {
            EvaluationTag evaluationTag = evaluationTagRepository.findById(analysisTag.getEvaluationTagId())
                    .orElse(null);
            long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(analysisTag.getAnalysisTagId());
            
            return convertToResponse(analysisTag, 
                    evaluationTag != null ? evaluationTag.getModel() : null, 
                    (int) analysisCount);
        });
    }

    /**
     * Get analysis tags by evaluation tag ID
     */
    @Transactional(readOnly = true)
    public List<AnalysisTagResponse> getAnalysisTagsByEvaluationTagId(Long evaluationTagId) {
        log.info("Getting analysis tags by evaluation tag ID: {}", evaluationTagId);

        List<AnalysisTag> analysisTags = analysisTagRepository.findByEvaluationTagId(evaluationTagId);
        
        EvaluationTag evaluationTag = evaluationTagRepository.findById(evaluationTagId)
                .orElse(null);
        String evaluationTagModel = evaluationTag != null ? evaluationTag.getModel() : null;

        return analysisTags.stream()
                .map(analysisTag -> {
                    long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(analysisTag.getAnalysisTagId());
                    return convertToResponse(analysisTag, evaluationTagModel, (int) analysisCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get analysis tags by model
     */
    @Transactional(readOnly = true)
    public List<AnalysisTagResponse> getAnalysisTagsByModel(String model) {
        log.info("Getting analysis tags by model: {}", model);

        List<AnalysisTag> analysisTags = analysisTagRepository.findByModel(model);
        
        return analysisTags.stream()
                .map(analysisTag -> {
                    EvaluationTag evaluationTag = evaluationTagRepository.findById(analysisTag.getEvaluationTagId())
                            .orElse(null);
                    long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(analysisTag.getAnalysisTagId());
                    
                    return convertToResponse(analysisTag, 
                            evaluationTag != null ? evaluationTag.getModel() : null, 
                            (int) analysisCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Update analysis tag
     */
    public AnalysisTagResponse updateAnalysisTag(Long id, AnalysisTagCreateRequest request) {
        log.info("Updating analysis tag with ID: {}", id);

        AnalysisTag analysisTag = analysisTagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Analysis tag not found with ID: " + id));

        // Check if evaluation tag exists
        EvaluationTag evaluationTag = evaluationTagRepository.findById(request.getEvaluationTagId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Evaluation tag not found with ID: " + request.getEvaluationTagId()));

        // Check for duplicate if evaluation tag or model changed
        if (!analysisTag.getEvaluationTagId().equals(request.getEvaluationTagId()) || 
            !analysisTag.getModel().equals(request.getModel())) {
            if (analysisTagRepository.existsByEvaluationTagIdAndModel(request.getEvaluationTagId(), request.getModel())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Analysis tag already exists for evaluation tag ID: " + 
                        request.getEvaluationTagId() + " and model: " + request.getModel());
            }
        }

        // Update fields
        analysisTag.setEvaluationTagId(request.getEvaluationTagId());
        analysisTag.setAnalysisTime(request.getAnalysisTime());
        analysisTag.setModel(request.getModel());

        AnalysisTag updatedTag = analysisTagRepository.save(analysisTag);
        long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(id);

        log.info("Updated analysis tag with ID: {}", id);
        return convertToResponse(updatedTag, evaluationTag.getModel(), (int) analysisCount);
    }

    /**
     * Delete analysis tag
     */
    public void deleteAnalysisTag(Long id) {
        log.info("Deleting analysis tag with ID: {}", id);

        if (!analysisTagRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Analysis tag not found with ID: " + id);
        }

        // Delete associated analysis results first
        evaluationAnalysisRepository.deleteByAnalysisTagId(id);
        
        // Delete the analysis tag
        analysisTagRepository.deleteById(id);
        
        log.info("Deleted analysis tag with ID: {}", id);
    }

    /**
     * Convert AnalysisTag entity to response DTO
     */
    private AnalysisTagResponse convertToResponse(AnalysisTag analysisTag, String evaluationTagModel, Integer analysisCount) {
        AnalysisTagResponse response = modelMapper.map(analysisTag, AnalysisTagResponse.class);
        response.setEvaluationTagModel(evaluationTagModel);
        response.setAnalysisCount(analysisCount);
        return response;
    }
} 