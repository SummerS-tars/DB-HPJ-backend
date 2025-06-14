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
import org.springframework.web.multipart.MultipartFile;
import top.thesumst.llm_eval_backend.dto.request.EvaluationResultImportRequest;
import top.thesumst.llm_eval_backend.dto.request.EvaluationResultStatusUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationResultResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.entity.EvaluationResult;
import top.thesumst.llm_eval_backend.entity.EvaluationTag;
import top.thesumst.llm_eval_backend.entity.StandardQuestion;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.EvaluationResultRepository;
import top.thesumst.llm_eval_backend.repository.EvaluationTagRepository;
import top.thesumst.llm_eval_backend.repository.StandardQuestionRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for evaluation result operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultService {

    private final EvaluationResultRepository evaluationResultRepository;
    private final EvaluationTagRepository evaluationTagRepository;
    private final StandardQuestionRepository standardQuestionRepository;
    private final ModelMapper modelMapper;

    /**
     * Import evaluation results from CSV file
     */
    @Transactional
    public ImportResponse importFromFile(MultipartFile file, Long evaluationTagId) {
        log.info("Starting import of evaluation results from file: {}, evaluation tag: {}", 
                file.getOriginalFilename(), evaluationTagId);

        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.IMPORT_DATA_INVALID, "上传文件不能为空");
        }

        // Validate evaluation tag exists
        EvaluationTag evaluationTag = evaluationTagRepository.findById(evaluationTagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估标签不存在，ID: " + evaluationTagId));

        List<ImportResponse.ImportError> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    EvaluationResultImportRequest request = parseCsvLine(line, evaluationTagId);
                    
                    // Check for duplicate
                    if (evaluationResultRepository.existsByEvaluationTagIdAndStdQuestionId(
                            request.getEvaluationTagId(), request.getStdQuestionId())) {
                        log.warn("Duplicate evaluation result found: tagId={}, questionId={}", 
                                request.getEvaluationTagId(), request.getStdQuestionId());
                        continue;
                    }

                    // Validate standard question exists
                    if (!standardQuestionRepository.existsById(request.getStdQuestionId())) {
                        throw new IllegalArgumentException("标准问题不存在，ID: " + request.getStdQuestionId());
                    }

                    EvaluationResult result = modelMapper.map(request, EvaluationResult.class);
                    evaluationResultRepository.save(result);
                    importedCount++;
                    
                } catch (Exception e) {
                    log.error("Failed to process line: {}", line, e);
                    errors.add(ImportResponse.ImportError.builder()
                            .originalRecord(line)
                            .error(e.getMessage())
                            .build());
                    failedCount++;
                }
            }
        } catch (Exception e) {
            log.error("Failed to read import file", e);
            throw new BusinessException(ErrorCode.IMPORT_PARSE_ERROR, "文件解析失败: " + e.getMessage());
        }

        log.info("Import completed. Imported: {}, Failed: {}", importedCount, failedCount);
        
        return ImportResponse.builder()
                .message("评估结果导入成功")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Batch import evaluation results from request list
     */
    @Transactional
    public ImportResponse batchImport(List<EvaluationResultImportRequest> requests) {
        log.info("Starting batch import of {} evaluation results", requests.size());

        List<ImportResponse.ImportError> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;

        for (EvaluationResultImportRequest request : requests) {
            try {
                // Validate evaluation tag exists
                if (!evaluationTagRepository.existsById(request.getEvaluationTagId())) {
                    throw new IllegalArgumentException("评估标签不存在，ID: " + request.getEvaluationTagId());
                }

                // Validate standard question exists
                if (!standardQuestionRepository.existsById(request.getStdQuestionId())) {
                    throw new IllegalArgumentException("标准问题不存在，ID: " + request.getStdQuestionId());
                }

                // Check for duplicate
                if (evaluationResultRepository.existsByEvaluationTagIdAndStdQuestionId(
                        request.getEvaluationTagId(), request.getStdQuestionId())) {
                    log.warn("Duplicate evaluation result found: tagId={}, questionId={}", 
                            request.getEvaluationTagId(), request.getStdQuestionId());
                    continue;
                }

                EvaluationResult result = modelMapper.map(request, EvaluationResult.class);
                evaluationResultRepository.save(result);
                importedCount++;
                
            } catch (Exception e) {
                log.error("Failed to process evaluation result: tagId={}, questionId={}", 
                        request.getEvaluationTagId(), request.getStdQuestionId(), e);
                errors.add(ImportResponse.ImportError.builder()
                        .originalRecord(request.toString())
                        .error(e.getMessage())
                        .build());
                failedCount++;
            }
        }

        log.info("Batch import completed. Imported: {}, Failed: {}", importedCount, failedCount);
        
        return ImportResponse.builder()
                .message("评估结果批量导入成功")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Get evaluation results with pagination and filtering
     */
    public Page<EvaluationResultResponse> getEvaluationResults(int page, int size, String sortBy, String order,
                                                               Long evaluationTagId, EvaluationResultStatus status, 
                                                               QuestionType type) {
        
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EvaluationResult> resultPage;
        
        if (evaluationTagId != null && status != null && type != null) {
            resultPage = evaluationResultRepository.findByEvaluationTagIdAndStatusAndType(evaluationTagId, status, type, pageable);
        } else if (evaluationTagId != null && status != null) {
            resultPage = evaluationResultRepository.findByEvaluationTagIdAndStatus(evaluationTagId, status, pageable);
        } else if (evaluationTagId != null && type != null) {
            resultPage = evaluationResultRepository.findByEvaluationTagIdAndType(evaluationTagId, type, pageable);
        } else if (evaluationTagId != null) {
            resultPage = evaluationResultRepository.findByEvaluationTagId(evaluationTagId, pageable);
        } else if (status != null) {
            resultPage = evaluationResultRepository.findByStatus(status, pageable);
        } else if (type != null) {
            resultPage = evaluationResultRepository.findByType(type, pageable);
        } else {
            resultPage = evaluationResultRepository.findAll(pageable);
        }
        
        return resultPage.map(this::convertToResponse);
    }

    /**
     * Get evaluation result by ID
     */
    public EvaluationResultResponse getEvaluationResultById(Long id) {
        EvaluationResult result = evaluationResultRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估结果不存在，ID: " + id));
        
        return convertToResponse(result);
    }

    /**
     * Update evaluation result status
     */
    @Transactional
    public EvaluationResultResponse updateStatus(Long id, EvaluationResultStatusUpdateRequest request) {
        EvaluationResult result = evaluationResultRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估结果不存在，ID: " + id));
        
        result.setStatus(request.getStatus());
        EvaluationResult savedResult = evaluationResultRepository.save(result);
        
        log.info("Updated evaluation result status: id={}, status={}", id, request.getStatus());
        
        return convertToResponse(savedResult);
    }

    /**
     * Export evaluation results to CSV format
     */
    public String exportEvaluationResults(Long evaluationTagId, String format) {
        log.info("Exporting evaluation results for tag: {}, format: {}", evaluationTagId, format);

        // Validate evaluation tag exists
        EvaluationTag evaluationTag = evaluationTagRepository.findById(evaluationTagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "评估标签不存在，ID: " + evaluationTagId));

        List<EvaluationResult> results = evaluationResultRepository.findForExport(evaluationTagId);
        
        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(results, evaluationTag);
        } else if ("json".equalsIgnoreCase(format)) {
            return exportToJson(results, evaluationTag);
        } else {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "不支持的导出格式: " + format);
        }
    }

    /**
     * Parse CSV line to EvaluationResultImportRequest
     * Expected format: stdQuestionId,content,type,status
     */
    private EvaluationResultImportRequest parseCsvLine(String line, Long evaluationTagId) {
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        
        if (fields.length < 3) {
            throw new IllegalArgumentException("CSV line format invalid: " + line);
        }

        EvaluationResultImportRequest request = new EvaluationResultImportRequest();
        request.setEvaluationTagId(evaluationTagId);
        
        // Parse fields
        request.setStdQuestionId(Long.parseLong(cleanCsvField(fields[0])));
        request.setContent(cleanCsvField(fields[1]));
        request.setType(QuestionType.valueOf(cleanCsvField(fields[2]).toUpperCase()));
        
        if (fields.length > 3) {
            request.setStatus(EvaluationResultStatus.valueOf(cleanCsvField(fields[3]).toUpperCase()));
        }
        
        return request;
    }

    /**
     * Export results to CSV format
     */
    private String exportToCsv(List<EvaluationResult> results, EvaluationTag tag) {
        StringBuilder csv = new StringBuilder();
        csv.append("Model,DataSetVersion,StdQuestionId,QuestionTitle,Content,Type,Status\n");
        
        for (EvaluationResult result : results) {
                         csv.append(String.format("\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                     tag.getModel(),
                     tag.getDataSetVersion(),
                     result.getStdQuestionId(),
                     result.getStandardQuestion().getContent().replace("\"", "\"\""),
                     result.getContent().replace("\"", "\"\""),
                     result.getType(),
                     result.getStatus()));
        }
        
        return csv.toString();
    }

    /**
     * Export results to JSON format
     */
    private String exportToJson(List<EvaluationResult> results, EvaluationTag tag) {
        // Simple JSON export - could use Jackson ObjectMapper for more complex JSON
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"model\": \"").append(tag.getModel()).append("\",\n");
        json.append("  \"dataSetVersion\": \"").append(tag.getDataSetVersion()).append("\",\n");
        json.append("  \"evaluationTime\": ").append(tag.getEvaluationTime()).append(",\n");
        json.append("  \"results\": [\n");
        
        for (int i = 0; i < results.size(); i++) {
            EvaluationResult result = results.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(result.getId()).append(",\n");
            json.append("      \"stdQuestionId\": ").append(result.getStdQuestionId()).append(",\n");
                         json.append("      \"questionContent\": \"").append(result.getStandardQuestion().getContent().replace("\"", "\\\"")).append("\",\n");
            json.append("      \"content\": \"").append(result.getContent().replace("\"", "\\\"")).append("\",\n");
            json.append("      \"type\": \"").append(result.getType()).append("\",\n");
            json.append("      \"status\": \"").append(result.getStatus()).append("\"\n");
            json.append("    }").append(i < results.size() - 1 ? "," : "").append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }

    /**
     * Convert entity to response DTO
     */
    private EvaluationResultResponse convertToResponse(EvaluationResult result) {
        EvaluationResultResponse response = modelMapper.map(result, EvaluationResultResponse.class);
        
        // Add related information
        if (result.getEvaluationTag() != null) {
            response.setModel(result.getEvaluationTag().getModel());
            response.setDataSetVersion(result.getEvaluationTag().getDataSetVersion());
        }
        
                 if (result.getStandardQuestion() != null) {
             response.setQuestionTitle(""); // No title field in StandardQuestion
             response.setQuestionContent(result.getStandardQuestion().getContent());
         }
        
        return response;
    }

    /**
     * Clean CSV field by removing quotes and trimming
     */
    private String cleanCsvField(String field) {
        if (field == null) return "";
        return field.trim().replaceAll("^\"|\"$", "");
    }
} 