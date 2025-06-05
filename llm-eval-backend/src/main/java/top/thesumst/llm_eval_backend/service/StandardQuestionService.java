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
import top.thesumst.llm_eval_backend.dto.request.StandardQuestionImportRequest;
import top.thesumst.llm_eval_backend.dto.request.TagAddRequest;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.StandardQuestionResponse;
import top.thesumst.llm_eval_backend.entity.StandardQuestion;
import top.thesumst.llm_eval_backend.entity.Tag;
import top.thesumst.llm_eval_backend.entity.Version;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.RawQuestionRepository;
import top.thesumst.llm_eval_backend.repository.StandardQuestionRepository;
import top.thesumst.llm_eval_backend.repository.TagRepository;
import top.thesumst.llm_eval_backend.repository.VersionRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for standard question management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StandardQuestionService {

    private final StandardQuestionRepository standardQuestionRepository;
    private final RawQuestionRepository rawQuestionRepository;
    private final VersionRepository versionRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    /**
     * Import standard questions from list
     */
    @Transactional
    public ImportResponse importStandardQuestions(List<StandardQuestionImportRequest> requests) {
        log.info("Starting import of {} standard questions", requests.size());

        List<ImportResponse.ImportError> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            StandardQuestionImportRequest request = requests.get(i);
            try {
                // Validate original raw question exists
                if (!rawQuestionRepository.existsById(request.getOriginalRawQuestionId())) {
                    throw new IllegalArgumentException("原始问题不存在，ID: " + request.getOriginalRawQuestionId());
                }

                StandardQuestion question = modelMapper.map(request, StandardQuestion.class);
                
                // Set up versions
                if (request.getVersionIds() != null && !request.getVersionIds().isEmpty()) {
                    Set<Version> versions = new HashSet<>();
                    for (String versionId : request.getVersionIds()) {
                        Version version = versionRepository.findById(versionId)
                                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + versionId));
                        versions.add(version);
                    }
                    question.setVersions(versions);
                }

                // Set up tags
                if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
                    Set<Tag> tags = new HashSet<>();
                    for (String tagName : request.getTagNames()) {
                        Tag tag = tagRepository.findById(tagName).orElseGet(() -> {
                            // Create tag if it doesn't exist
                            Tag newTag = new Tag();
                            newTag.setTag(tagName);
                            return tagRepository.save(newTag);
                        });
                        tags.add(tag);
                    }
                    question.setTags(tags);
                }

                standardQuestionRepository.save(question);
                importedCount++;

            } catch (Exception e) {
                log.error("Failed to process standard question at index {}: {}", i, e.getMessage(), e);
                errors.add(ImportResponse.ImportError.builder()
                        .originalRecord("Request " + i + ": " + request.toString())
                        .error(e.getMessage())
                        .build());
                failedCount++;
            }
        }

        log.info("Import completed. Imported: {}, Failed: {}", importedCount, failedCount);
        
        return ImportResponse.builder()
                .message("标准问题导入完成")
                .importedCount(importedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Get standard questions with pagination and filtering
     */
    public Page<StandardQuestionResponse> getStandardQuestions(int page, int size, String sortBy, String order,
                                                              QuestionType type, StandardQuestionStatus status,
                                                              String version, String tags, Long originalRawQuestionId) {
        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StandardQuestion> questionPage;

        // Handle multiple tags filter
        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagArray = tags.split(",");
            String firstTag = tagArray[0].trim();
            
            if (tagArray.length == 1) {
                // Single tag filter with other filters
                questionPage = standardQuestionRepository.findByFilters(type, status, version, originalRawQuestionId, firstTag, pageable);
            } else {
                // Multiple tags - find questions that have ALL specified tags
                questionPage = standardQuestionRepository.findByAllTags(tagArray, tagArray.length, pageable);
            }
        } else {
            // No tag filter, use other filters
            questionPage = standardQuestionRepository.findByFilters(type, status, version, originalRawQuestionId, null, pageable);
        }
        
        return questionPage.map(question -> modelMapper.map(question, StandardQuestionResponse.class));
    }

    /**
     * Get standard question by ID
     */
    public StandardQuestionResponse getStandardQuestionById(Long id) {
        StandardQuestion question = standardQuestionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标准问题不存在，ID: " + id));
        
        return modelMapper.map(question, StandardQuestionResponse.class);
    }

    /**
     * Get standard questions by raw question ID
     */
    public Page<StandardQuestionResponse> getStandardQuestionsByRawQuestionId(Long rawQuestionId, int page, int size, 
                                                                              String sortBy, String order) {
        // Validate that raw question exists
        if (!rawQuestionRepository.existsById(rawQuestionId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "原始问题不存在，ID: " + rawQuestionId);
        }

        Sort sort = "desc".equalsIgnoreCase(order) ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StandardQuestion> questionPage = standardQuestionRepository.findByOriginalRawQuestionId(rawQuestionId, pageable);
        
        return questionPage.map(question -> modelMapper.map(question, StandardQuestionResponse.class));
    }

    /**
     * Add tag to standard question
     */
    @Transactional
    public StandardQuestionResponse addTagToQuestion(Long questionId, TagAddRequest request) {
        log.info("Adding tag '{}' to standard question {}", request.getTagName(), questionId);

        StandardQuestion question = standardQuestionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标准问题不存在，ID: " + questionId));

        // Find or create tag
        Tag tag = tagRepository.findById(request.getTagName()).orElseGet(() -> {
            Tag newTag = new Tag();
            newTag.setTag(request.getTagName());
            return tagRepository.save(newTag);
        });

        // Add tag to question if not already present
        if (question.getTags() == null) {
            question.setTags(new HashSet<>());
        }
        
        if (question.getTags().contains(tag)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                    "标签已存在于此问题: " + request.getTagName());
        }

        question.getTags().add(tag);
        StandardQuestion savedQuestion = standardQuestionRepository.save(question);

        log.info("Added tag '{}' to standard question {}", request.getTagName(), questionId);
        
        return modelMapper.map(savedQuestion, StandardQuestionResponse.class);
    }

    /**
     * Remove tag from standard question
     */
    @Transactional
    public StandardQuestionResponse removeTagFromQuestion(Long questionId, String tagName) {
        log.info("Removing tag '{}' from standard question {}", tagName, questionId);

        StandardQuestion question = standardQuestionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标准问题不存在，ID: " + questionId));

        Tag tag = tagRepository.findById(tagName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标签不存在: " + tagName));

        if (question.getTags() == null || !question.getTags().contains(tag)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "问题未关联此标签: " + tagName);
        }

        question.getTags().remove(tag);
        StandardQuestion savedQuestion = standardQuestionRepository.save(question);

        log.info("Removed tag '{}' from standard question {}", tagName, questionId);
        
        return modelMapper.map(savedQuestion, StandardQuestionResponse.class);
    }

    /**
     * Update standard question status
     */
    @Transactional
    public StandardQuestionResponse updateStatus(Long id, StandardQuestionStatus status) {
        StandardQuestion question = standardQuestionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标准问题不存在，ID: " + id));
        
        question.setStatus(status);
        StandardQuestion savedQuestion = standardQuestionRepository.save(question);
        
        log.info("Updated standard question status: id={}, status={}", id, status);
        
        return modelMapper.map(savedQuestion, StandardQuestionResponse.class);
    }
} 