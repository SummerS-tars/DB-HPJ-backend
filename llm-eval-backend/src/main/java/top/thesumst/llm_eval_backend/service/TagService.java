package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.request.TagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.TagResponse;
import top.thesumst.llm_eval_backend.entity.Tag;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for tag management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new tag
     */
    @Transactional
    public TagResponse createTag(TagCreateRequest request) {
        log.info("Creating new tag: {}", request.getTag());

        // Check if tag already exists
        if (tagRepository.existsByTag(request.getTag())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                    "标签已存在: " + request.getTag());
        }

        Tag tag = new Tag();
        tag.setTag(request.getTag());
        
        Tag savedTag = tagRepository.save(tag);
        
        log.info("Created tag: {}", savedTag.getTag());
        
        TagResponse response = modelMapper.map(savedTag, TagResponse.class);
        response.setQuestionCount(0L);
        return response;
    }

    /**
     * Get all tags
     */
    public List<TagResponse> getAllTags() {
        log.info("Fetching all tags");

        List<Object[]> tagsWithCount = tagRepository.findAllWithQuestionCount();
        
        return tagsWithCount.stream()
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long questionCount = (Long) result[1];
                    
                    TagResponse response = modelMapper.map(tag, TagResponse.class);
                    response.setQuestionCount(questionCount);
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get tag by name
     */
    public TagResponse getTagByName(String tagName) {
        log.info("Fetching tag: {}", tagName);

        Tag tag = tagRepository.findById(tagName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标签不存在: " + tagName));

        TagResponse response = modelMapper.map(tag, TagResponse.class);
        response.setQuestionCount((long) tag.getStandardQuestions().size());
        
        return response;
    }

    /**
     * Search tags by name
     */
    public List<TagResponse> searchTags(String query) {
        log.info("Searching tags with query: {}", query);

        List<Tag> tags = tagRepository.findByTagContainingIgnoreCase(query);
        
        return tags.stream()
                .map(tag -> {
                    TagResponse response = modelMapper.map(tag, TagResponse.class);
                    response.setQuestionCount((long) tag.getStandardQuestions().size());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if tag exists
     */
    public boolean existsByTag(String tag) {
        return tagRepository.existsByTag(tag);
    }

    /**
     * Delete tag (only if no standard questions are associated)
     */
    @Transactional
    public void deleteTag(String tagName) {
        log.info("Deleting tag: {}", tagName);

        Tag tag = tagRepository.findById(tagName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "标签不存在: " + tagName));

        if (!tag.getStandardQuestions().isEmpty()) {
            throw new BusinessException(ErrorCode.CONSTRAINT_VIOLATION, 
                    "无法删除标签，仍有标准问题使用此标签: " + tagName);
        }

        tagRepository.delete(tag);
        log.info("Deleted tag: {}", tagName);
    }
} 