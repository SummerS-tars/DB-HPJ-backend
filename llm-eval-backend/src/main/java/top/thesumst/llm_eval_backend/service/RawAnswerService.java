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
import top.thesumst.llm_eval_backend.dto.request.RawAnswerImportRequest;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.RawAnswerResponse;
import top.thesumst.llm_eval_backend.entity.RawAnswer;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.RawAnswerRepository;
import top.thesumst.llm_eval_backend.repository.RawQuestionRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for raw answer operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RawAnswerService {

    private final RawAnswerRepository rawAnswerRepository;
    private final RawQuestionRepository rawQuestionRepository;
    private final ModelMapper modelMapper;

    /**
     * Import raw answers from file
     */
    @Transactional
    public ImportResponse importFromFile(MultipartFile file, String sourcePlatform) {
        log.info("Starting import of raw answers from file: {}, source platform: {}", 
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
                    RawAnswerImportRequest request = parseCsvLine(line, sourcePlatform);
                    
                    // Validate that raw question exists
                    if (!rawQuestionRepository.existsById(request.getRawQuestionId())) {
                        log.warn("Raw question not found: id={}", request.getRawQuestionId());
                        errors.add(ImportResponse.ImportError.builder()
                                .originalRecord(line)
                                .error("原始问题不存在，ID: " + request.getRawQuestionId())
                                .build());
                        failedCount++;
                        continue;
                    }

                    // Check for duplicate
                    if (request.getPostId() != null && 
                        rawAnswerRepository.existsByPostIdAndSourcePlatform(request.getPostId(), sourcePlatform)) {
                        log.warn("Duplicate answer found: postId={}, platform={}", 
                                request.getPostId(), sourcePlatform);
                        continue;
                    }

                    RawAnswer answer = modelMapper.map(request, RawAnswer.class);
                    rawAnswerRepository.save(answer);
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
                .message("原始答案导入成功")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Get raw answers with pagination and filtering
     */
    public Page<RawAnswerResponse> getRawAnswers(int page, int size, String sortBy, String order, 
                                                 String sourcePlatform) {
        
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RawAnswer> answerPage;
        
        if (sourcePlatform != null) {
            answerPage = rawAnswerRepository.findBySourcePlatform(sourcePlatform, pageable);
        } else {
            answerPage = rawAnswerRepository.findAll(pageable);
        }
        
        return answerPage.map(answer -> modelMapper.map(answer, RawAnswerResponse.class));
    }

    /**
     * Get raw answers by question ID
     */
    public Page<RawAnswerResponse> getRawAnswersByQuestionId(Long questionId, int page, int size, 
                                                             String sortBy, String order) {
        
        // Validate that question exists
        if (!rawQuestionRepository.existsById(questionId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "原始问题不存在，ID: " + questionId);
        }
        
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RawAnswer> answerPage = rawAnswerRepository.findByRawQuestionId(questionId, pageable);
        
        return answerPage.map(answer -> modelMapper.map(answer, RawAnswerResponse.class));
    }

    /**
     * Get raw answer by ID
     */
    public RawAnswerResponse getRawAnswerById(Long id) {
        RawAnswer answer = rawAnswerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "原始答案不存在，ID: " + id));
        
        return modelMapper.map(answer, RawAnswerResponse.class);
    }

    /**
     * Parse CSV line to RawAnswerImportRequest
     * Expected format: rawQuestionId,content,sourcePlatform,postId,score
     */
    private RawAnswerImportRequest parseCsvLine(String line, String sourcePlatform) {
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Handle CSV with quotes
        
        if (fields.length < 2) {
            throw new IllegalArgumentException("CSV line format invalid: " + line);
        }

        RawAnswerImportRequest request = new RawAnswerImportRequest();
        
        // Parse rawQuestionId
        try {
            request.setRawQuestionId(Long.parseLong(cleanCsvField(fields[0])));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rawQuestionId format: " + fields[0]);
        }
        
        request.setContent(fields.length > 1 ? cleanCsvField(fields[1]) : null);
        request.setSourcePlatform(sourcePlatform);
        
        // Parse postId
        if (fields.length > 2 && !fields[2].trim().isEmpty()) {
            try {
                request.setPostId(Integer.parseInt(cleanCsvField(fields[2])));
            } catch (NumberFormatException e) {
                log.warn("Invalid postId format: {}", fields[2]);
            }
        }
        
        // Parse score
        if (fields.length > 3 && !fields[3].trim().isEmpty()) {
            try {
                request.setScore(Integer.parseInt(cleanCsvField(fields[3])));
            } catch (NumberFormatException e) {
                log.warn("Invalid score format: {}", fields[3]);
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