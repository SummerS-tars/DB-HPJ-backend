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
import top.thesumst.llm_eval_backend.dto.request.RawQuestionImportRequest;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.RawQuestionResponse;
import top.thesumst.llm_eval_backend.entity.RawQuestion;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.RawQuestionRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for raw question operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;
    private final ModelMapper modelMapper;

    /**
     * Import raw questions from file
     */
    @Transactional
    public ImportResponse importFromFile(MultipartFile file, String sourcePlatform) {
        log.info("Starting import of raw questions from file: {}, source platform: {}", 
                file.getOriginalFilename(), sourcePlatform);

        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.IMPORT_DATA_INVALID, "上传文件不能为空");
        }

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
                    RawQuestionImportRequest request = parseCsvLine(line, sourcePlatform);
                    
                    // Check for duplicate
                    if (request.getPostId() != null && 
                        rawQuestionRepository.existsByPostIdAndSourcePlatform(request.getPostId(), sourcePlatform)) {
                        log.warn("Duplicate question found: postId={}, platform={}", 
                                request.getPostId(), sourcePlatform);
                        continue;
                    }

                    RawQuestion question = modelMapper.map(request, RawQuestion.class);
                    question.setStatus(RawQuestionStatus.WAITING_CONVERTED);
                    
                    rawQuestionRepository.save(question);
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
                .message("原始问题导入成功")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Get raw questions with pagination and filtering
     */
    public Page<RawQuestionResponse> getRawQuestions(int page, int size, String sortBy, String order, 
                                                     RawQuestionStatus status, String sourcePlatform) {
        
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RawQuestion> questionPage;
        
        if (status != null && sourcePlatform != null) {
            questionPage = rawQuestionRepository.findByStatusAndSourcePlatform(status, sourcePlatform, pageable);
        } else if (status != null) {
            questionPage = rawQuestionRepository.findByStatus(status, pageable);
        } else if (sourcePlatform != null) {
            questionPage = rawQuestionRepository.findBySourcePlatform(sourcePlatform, pageable);
        } else {
            questionPage = rawQuestionRepository.findAll(pageable);
        }
        
        return questionPage.map(question -> modelMapper.map(question, RawQuestionResponse.class));
    }

    /**
     * Get raw question by ID
     */
    public RawQuestionResponse getRawQuestionById(Integer id) {
        RawQuestion question = rawQuestionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "原始问题不存在，ID: " + id));
        
        return modelMapper.map(question, RawQuestionResponse.class);
    }

    /**
     * Update raw question status
     */
    @Transactional
    public RawQuestionResponse updateStatus(Integer id, RawQuestionStatus status) {
        RawQuestion question = rawQuestionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "原始问题不存在，ID: " + id));
        
        question.setStatus(status);
        RawQuestion savedQuestion = rawQuestionRepository.save(question);
        
        log.info("Updated raw question status: id={}, status={}", id, status);
        
        return modelMapper.map(savedQuestion, RawQuestionResponse.class);
    }

    /**
     * Parse CSV line to RawQuestionImportRequest
     * Expected format: title,content,sourcePlatform,tags,postId,score
     */
    private RawQuestionImportRequest parseCsvLine(String line, String sourcePlatform) {
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Handle CSV with quotes
        
        if (fields.length < 2) {
            throw new IllegalArgumentException("CSV line format invalid: " + line);
        }

        RawQuestionImportRequest request = new RawQuestionImportRequest();
        
        // Remove quotes if present
        request.setTitle(cleanCsvField(fields[0]));
        request.setContent(fields.length > 1 ? cleanCsvField(fields[1]) : null);
        request.setSourcePlatform(sourcePlatform);
        request.setTags(fields.length > 2 ? cleanCsvField(fields[2]) : null);
        
        // Parse postId
        if (fields.length > 3 && !fields[3].trim().isEmpty()) {
            try {
                request.setPostId(Integer.parseInt(cleanCsvField(fields[3])));
            } catch (NumberFormatException e) {
                log.warn("Invalid postId format: {}", fields[3]);
            }
        }
        
        // Parse score
        if (fields.length > 4 && !fields[4].trim().isEmpty()) {
            try {
                request.setScore(Integer.parseInt(cleanCsvField(fields[4])));
            } catch (NumberFormatException e) {
                log.warn("Invalid score format: {}", fields[4]);
            }
        }
        
        return request;
    }

    /**
     * Clean CSV field by removing quotes and trimming
     */
    private String cleanCsvField(String field) {
        if (field == null) return null;
        
        field = field.trim();
        if (field.startsWith("\"") && field.endsWith("\"") && field.length() > 1) {
            field = field.substring(1, field.length() - 1);
        }
        
        return field.isEmpty() ? null : field;
    }
} 