package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.thesumst.llm_eval_backend.dto.request.CandidateAnswerImportRequest;
import top.thesumst.llm_eval_backend.dto.request.CandidateAnswerStatusUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.CandidateAnswerResponse;
import top.thesumst.llm_eval_backend.dto.response.CandidateAnswerStatisticsResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.entity.*;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.CandidateAnswerRepository;
import top.thesumst.llm_eval_backend.repository.StandardQuestionRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing candidate answers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateAnswerService {

    private final CandidateAnswerRepository candidateAnswerRepository;
    private final StandardQuestionRepository standardQuestionRepository;
    private final StandardAnswerService standardAnswerService;

    /**
     * Import candidate answers from CSV file
     * 
     * CSV Format for OBJECTIVE questions:
     * Header: std_question_id,obj_answer,notes
     * Example: 1,A,Correct answer for multiple choice question
     * Example: 2,TRUE,Correct answer for true/false question
     * Note: obj_answer should be the CORRECT answer choice (A/B/C/D for multiple choice, TRUE/FALSE for boolean)
     * 
     * CSV Format for SUBJECTIVE questions:
     * Header: std_question_id,sub_answer,notes
     * Example: 5,"This is a comprehensive answer text",High quality answer
     * 
     * @param file CSV file containing candidate answers
     * @param type Question type (OBJECTIVE or SUBJECTIVE)
     * @return ImportResponse with import results and any errors
     */
    @Transactional
    public ImportResponse importCandidateAnswers(MultipartFile file, QuestionType type) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.IMPORT_DATA_INVALID, "上传文件不能为空");
        }

        List<ImportResponse.ImportError> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            // Skip header line
            reader.readLine();
            lineNumber++;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    CandidateAnswerImportRequest importRequest = parseCsvLine(line, type, lineNumber);
                    createCandidateAnswer(importRequest, type);
                    importedCount++;
                } catch (Exception e) {
                    failedCount++;
                    errors.add(ImportResponse.ImportError.builder()
                            .originalRecord(line)
                            .error(e.getMessage())
                            .build());
                    log.error("Failed to import candidate answer at line {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.IMPORT_PARSE_ERROR, "导入候选答案失败: " + e.getMessage());
        }

        return ImportResponse.builder()
                .message("候选答案导入完成")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors)
                .build();
    }

    /**
     * Parse CSV line into import request
     * Handles CSV fields with commas properly by respecting quoted fields
     */
    private CandidateAnswerImportRequest parseCsvLine(String line, QuestionType type, int lineNumber) {
        // Use regex to split CSV line while respecting quoted fields
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        
        if (fields.length < 2) {
            throw new BusinessException(ErrorCode.IMPORT_DATA_INVALID, "CSV格式错误，至少需要2列");
        }

        CandidateAnswerImportRequest request = new CandidateAnswerImportRequest();
        
        try {
            request.setStdQuestionId(Long.parseLong(cleanCsvField(fields[0])));
            
            if (type == QuestionType.OBJECTIVE) {
                request.setObjAnswer(ObjectiveAnswer.valueOf(cleanCsvField(fields[1]).toUpperCase()));
            } else {
                request.setSubAnswer(cleanCsvField(fields[1]));
            }
            
            if (fields.length > 2) {
                request.setNotes(cleanCsvField(fields[2]));
            }
            
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "标准问题ID格式错误");
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "答案格式错误");
        }

        return request;
    }

    /**
     * Clean CSV field by removing quotes and trimming whitespace
     * Handles quoted fields that may contain commas
     */
    private String cleanCsvField(String field) {
        if (field == null) return null;
        
        field = field.trim();
        
        // Remove surrounding quotes if present
        if (field.startsWith("\"") && field.endsWith("\"") && field.length() > 1) {
            field = field.substring(1, field.length() - 1);
            // Handle escaped quotes within the field (double quotes become single quotes)
            field = field.replace("\"\"", "\"");
        }
        
        return field.isEmpty() ? null : field;
    }

    /**
     * Create candidate answer from import request
     */
    @Transactional
    public CandidateAnswer createCandidateAnswer(CandidateAnswerImportRequest request, QuestionType type) {
        // Validate standard question exists
        StandardQuestion stdQuestion = standardQuestionRepository.findById(request.getStdQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标准问题不存在: " + request.getStdQuestionId()));

        // Validate type matches
        if (stdQuestion.getType() != type) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, 
                    String.format("问题类型不匹配，期望: %s, 实际: %s", type, stdQuestion.getType()));
        }

        // Create candidate answer
        CandidateAnswer candidateAnswer = new CandidateAnswer();
        candidateAnswer.setStdQuestionId(request.getStdQuestionId());
        candidateAnswer.setType(type);
        candidateAnswer.setStatus(CandidateAnswerStatus.PENDING);

        candidateAnswer = candidateAnswerRepository.save(candidateAnswer);

        // Set notes if provided
        candidateAnswer.setNotes(request.getNotes());

        // Create answer content based on type
        if (type == QuestionType.OBJECTIVE) {
            CandidateAnswerObj objAnswer = new CandidateAnswerObj();
            objAnswer.setCandidateAnswerId(candidateAnswer.getId());
            objAnswer.setObjAnswer(request.getObjAnswer());
            objAnswer.setCandidateAnswer(candidateAnswer);
            candidateAnswer.setCandidateAnswerObj(objAnswer);
        } else {
            CandidateAnswerSub subAnswer = new CandidateAnswerSub();
            subAnswer.setCandidateAnswerId(candidateAnswer.getId());
            subAnswer.setSubAnswer(request.getSubAnswer());
            subAnswer.setCandidateAnswer(candidateAnswer);
            candidateAnswer.setCandidateAnswerSub(subAnswer);
        }

        return candidateAnswerRepository.save(candidateAnswer);
    }

    /**
     * Get candidate answers with filtering and pagination
     */
    public Page<CandidateAnswerResponse> getCandidateAnswers(
            Long stdQuestionId, QuestionType type, CandidateAnswerStatus status, 
            int page, int size, String sort, String direction) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<CandidateAnswer> candidateAnswers;

        // Apply filters
        if (stdQuestionId != null && type != null && status != null) {
            candidateAnswers = candidateAnswerRepository.findByStdQuestionIdAndTypeAndStatus(
                    stdQuestionId, type, status, pageable);
        } else if (stdQuestionId != null && type != null) {
            candidateAnswers = candidateAnswerRepository.findByStdQuestionIdAndType(
                    stdQuestionId, type, pageable);
        } else if (stdQuestionId != null && status != null) {
            candidateAnswers = candidateAnswerRepository.findByStdQuestionIdAndStatus(
                    stdQuestionId, status, pageable);
        } else if (type != null && status != null) {
            candidateAnswers = candidateAnswerRepository.findByTypeAndStatus(type, status, pageable);
        } else if (stdQuestionId != null) {
            candidateAnswers = candidateAnswerRepository.findByStdQuestionId(stdQuestionId, pageable);
        } else if (type != null) {
            candidateAnswers = candidateAnswerRepository.findByType(type, pageable);
        } else if (status != null) {
            candidateAnswers = candidateAnswerRepository.findByStatus(status, pageable);
        } else {
            candidateAnswers = candidateAnswerRepository.findAll(pageable);
        }

        return candidateAnswers.map(this::convertToResponse);
    }

    /**
     * Get candidate answer by ID
     */
    public CandidateAnswerResponse getCandidateAnswerById(Long id) {
        CandidateAnswer candidateAnswer = candidateAnswerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "候选答案不存在"));
        
        return convertToResponse(candidateAnswer);
    }

    /**
     * Update candidate answer status
     */
    @Transactional
    public CandidateAnswerResponse updateStatus(Long id, CandidateAnswerStatusUpdateRequest request) {
        CandidateAnswer candidateAnswer = candidateAnswerRepository.findWithContentById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "候选答案不存在"));

        CandidateAnswerStatus oldStatus = candidateAnswer.getStatus();
        candidateAnswer.setStatus(request.getStatus());
        candidateAnswer = candidateAnswerRepository.save(candidateAnswer);

        log.info("Updated candidate answer {} status from {} to {}, reason: {}", 
                id, oldStatus, request.getStatus(), request.getReason());

        // Automatically create standard answer when accepting candidate answer
        if (request.getStatus() == CandidateAnswerStatus.ACCEPTED && 
            oldStatus != CandidateAnswerStatus.ACCEPTED &&
            Boolean.TRUE.equals(request.getCreateStandardAnswer())) {
            
            try {
                // Check if standard answer already exists for this candidate answer
                if (!standardAnswerService.existsBySelectedFromCandidateId(id)) {
                    Integer score = request.getScore() != null ? request.getScore() : 8; // Default score
                    standardAnswerService.createFromCandidateAnswerId(id, score);
                    log.info("Automatically created standard answer from accepted candidate answer {} with score {}", 
                            id, score);
                } else {
                    log.info("Standard answer already exists for candidate answer {}, skipping creation", id);
                }
            } catch (Exception e) {
                log.error("Failed to automatically create standard answer from candidate answer {}: {}", 
                        id, e.getMessage());
                // Don't fail the status update if standard answer creation fails
                // This allows the user to manually create the standard answer later
            }
        }

        return convertToResponse(candidateAnswer);
    }

    /**
     * Get candidate answers for specific standard question
     */
    public List<CandidateAnswerResponse> getCandidateAnswersByStdQuestionId(Long stdQuestionId) {
        List<CandidateAnswer> candidateAnswers = candidateAnswerRepository
                .findWithContentByStdQuestionId(stdQuestionId);
        
        return candidateAnswers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get candidate answer statistics
     */
    public CandidateAnswerStatisticsResponse getStatistics() {
        CandidateAnswerStatisticsResponse response = new CandidateAnswerStatisticsResponse();
        
        response.setTotalCount(candidateAnswerRepository.count());
        
        // Status statistics
        Map<String, Long> statusStats = new HashMap<>();
        List<Object[]> statusResults = candidateAnswerRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            statusStats.put(result[0].toString(), (Long) result[1]);
        }
        response.setCountByStatus(statusStats);
        
        // Type statistics
        Map<String, Long> typeStats = new HashMap<>();
        List<Object[]> typeResults = candidateAnswerRepository.getTypeStatistics();
        for (Object[] result : typeResults) {
            typeStats.put(result[0].toString(), (Long) result[1]);
        }
        response.setCountByType(typeStats);
        
        // Type and status statistics
        Map<String, Long> typeStatusStats = new HashMap<>();
        List<Object[]> typeStatusResults = candidateAnswerRepository.getTypeAndStatusStatistics();
        for (Object[] result : typeStatusResults) {
            String key = result[0].toString() + "_" + result[1].toString();
            typeStatusStats.put(key, (Long) result[2]);
        }
        response.setCountByTypeAndStatus(typeStatusStats);
        
        return response;
    }

    /**
     * Delete candidate answer
     */
    @Transactional
    public void deleteCandidateAnswer(Long id) {
        if (!candidateAnswerRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "候选答案不存在");
        }
        
        candidateAnswerRepository.deleteById(id);
        log.info("Deleted candidate answer: {}", id);
    }

    /**
     * Convert entity to response DTO
     */
    private CandidateAnswerResponse convertToResponse(CandidateAnswer candidateAnswer) {
        CandidateAnswerResponse response = new CandidateAnswerResponse();
        response.setId(candidateAnswer.getId());
        response.setStdQuestionId(candidateAnswer.getStdQuestionId());
        response.setType(candidateAnswer.getType());
        response.setStatus(candidateAnswer.getStatus());
        response.setCreatedAt(LocalDateTime.now()); // TODO: Add createdAt field to entity
        response.setNotes(candidateAnswer.getNotes());

        // Set answer content based on type
        if (candidateAnswer.getType() == QuestionType.OBJECTIVE && 
            candidateAnswer.getCandidateAnswerObj() != null) {
            response.setObjAnswer(candidateAnswer.getCandidateAnswerObj().getObjAnswer());
        } else if (candidateAnswer.getType() == QuestionType.SUBJECTIVE && 
                   candidateAnswer.getCandidateAnswerSub() != null) {
            response.setSubAnswer(candidateAnswer.getCandidateAnswerSub().getSubAnswer());
        }

        // Set question info if available
        if (candidateAnswer.getStandardQuestion() != null) {
            StandardQuestion stdQuestion = candidateAnswer.getStandardQuestion();
            response.setQuestionContent(stdQuestion.getContent());
            // Use content as title for now, or set a default title
            response.setQuestionTitle("Standard Question " + stdQuestion.getId());
        }

        return response;
    }
} 