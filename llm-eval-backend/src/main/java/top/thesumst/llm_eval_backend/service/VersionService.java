package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.request.VersionCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.VersionResponse;
import top.thesumst.llm_eval_backend.entity.Version;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.VersionRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for version management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new version
     */
    @Transactional
    public VersionResponse createVersion(VersionCreateRequest request) {
        log.info("Creating new version: {}", request.getVersion());

        // Check if version already exists
        if (versionRepository.existsByVersion(request.getVersion())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                    "版本已存在: " + request.getVersion());
        }

        Version version = new Version();
        version.setVersion(request.getVersion());
        
        Version savedVersion = versionRepository.save(version);
        
        log.info("Created version: {}", savedVersion.getVersion());
        
        VersionResponse response = modelMapper.map(savedVersion, VersionResponse.class);
        response.setQuestionCount(0L);
        return response;
    }

    /**
     * Get all versions
     */
    public List<VersionResponse> getAllVersions() {
        log.info("Fetching all versions");

        List<Object[]> versionsWithCount = versionRepository.findAllWithQuestionCount();
        
        return versionsWithCount.stream()
                .map(result -> {
                    Version version = (Version) result[0];
                    Long questionCount = (Long) result[1];
                    
                    VersionResponse response = modelMapper.map(version, VersionResponse.class);
                    response.setQuestionCount(questionCount);
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get version by name
     */
    public VersionResponse getVersionByName(String versionName) {
        log.info("Fetching version: {}", versionName);

        Version version = versionRepository.findById(versionName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "版本不存在: " + versionName));

        VersionResponse response = modelMapper.map(version, VersionResponse.class);
        response.setQuestionCount((long) version.getStandardQuestions().size());
        
        return response;
    }

    /**
     * Check if version exists
     */
    public boolean existsByVersion(String version) {
        return versionRepository.existsByVersion(version);
    }

    /**
     * Delete version (only if no standard questions are associated)
     */
    @Transactional
    public void deleteVersion(String versionName) {
        log.info("Deleting version: {}", versionName);

        Version version = versionRepository.findById(versionName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "版本不存在: " + versionName));

        if (!version.getStandardQuestions().isEmpty()) {
            throw new BusinessException(ErrorCode.CONSTRAINT_VIOLATION, 
                    "无法删除版本，仍有标准问题关联此版本: " + versionName);
        }

        versionRepository.delete(version);
        log.info("Deleted version: {}", versionName);
    }
} 