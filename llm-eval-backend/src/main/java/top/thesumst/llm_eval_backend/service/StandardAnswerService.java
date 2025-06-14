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
import top.thesumst.llm_eval_backend.dto.request.StandardAnswerCreateRequest;
import top.thesumst.llm_eval_backend.dto.request.StandardAnswerUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.StandardAnswerResponse;
import top.thesumst.llm_eval_backend.dto.response.StandardAnswerStatisticsResponse;
import top.thesumst.llm_eval_backend.entity.*;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.CandidateAnswerRepository;
import top.thesumst.llm_eval_backend.repository.StandardAnswerRepository;
import top.thesumst.llm_eval_backend.repository.StandardQuestionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for standard answer management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StandardAnswerService {

    private final StandardAnswerRepository standardAnswerRepository;
    private final CandidateAnswerRepository candidateAnswerRepository;
    private final StandardQuestionRepository standardQuestionRepository;
    private final ModelMapper modelMapper;

    /**
     * Create standard answer from candidate answer
     */
    @Transactional
    public StandardAnswerResponse createFromCandidateAnswer(StandardAnswerCreateRequest request) {
        // Validate candidate answer exists and is accepted
        CandidateAnswer candidateAnswer = candidateAnswerRepository.findWithContentById(request.getCandidateAnswerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "候选答案不存在"));

        if (candidateAnswer.getStatus() != CandidateAnswerStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "只能从已接受的候选答案创建标准答案");
        }

        // Check if standard answer already exists for this candidate answer
        if (standardAnswerRepository.existsBySelectedFromCandidateId(request.getCandidateAnswerId())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "该候选答案已经被用于创建标准答案");
        }

        // Validate standard question exists
        StandardQuestion standardQuestion = standardQuestionRepository.findById(candidateAnswer.getStdQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标准问题不存在"));

        // Create standard answer
        StandardAnswer standardAnswer = new StandardAnswer();
        standardAnswer.setStdQuestionId(candidateAnswer.getStdQuestionId());
        standardAnswer.setType(candidateAnswer.getType());
        standardAnswer.setSelectedFromCandidateId(request.getCandidateAnswerId());
        standardAnswer.setScore(request.getScore());
        standardAnswer.setStatus(StandardAnswerStatus.ACCEPTED);
        standardAnswer.setCreatedAt(LocalDateTime.now());
        // Set notes: prioritize notes from request, fallback to candidate answer notes
        standardAnswer.setNotes(request.getNotes() != null ? request.getNotes() : candidateAnswer.getNotes());

        standardAnswer = standardAnswerRepository.save(standardAnswer);

        // Create answer content based on type
        if (candidateAnswer.getType() == QuestionType.OBJECTIVE) {
            StandardAnswerObj objAnswer = new StandardAnswerObj();
            objAnswer.setStdAnswerId(standardAnswer.getId());
            objAnswer.setObjAnswer(candidateAnswer.getCandidateAnswerObj().getObjAnswer());
            objAnswer.setStandardAnswer(standardAnswer);
            standardAnswer.setStandardAnswerObj(objAnswer);
        } else {
            StandardAnswerSub subAnswer = new StandardAnswerSub();
            subAnswer.setStdAnswerId(standardAnswer.getId());
            subAnswer.setSubAnswer(candidateAnswer.getCandidateAnswerSub().getSubAnswer());
            subAnswer.setStandardAnswer(standardAnswer);
            standardAnswer.setStandardAnswerSub(subAnswer);
        }

        standardAnswer = standardAnswerRepository.save(standardAnswer);

        log.info("Created standard answer {} from candidate answer {} with score {} and notes: {}", 
                standardAnswer.getId(), request.getCandidateAnswerId(), request.getScore(), 
                standardAnswer.getNotes());

        return convertToResponse(standardAnswer);
    }

    /**
     * Create standard answer from candidate answer by candidate ID
     */
    @Transactional
    public StandardAnswerResponse createFromCandidateAnswerId(Long candidateAnswerId, Integer score) {
        StandardAnswerCreateRequest request = new StandardAnswerCreateRequest();
        request.setCandidateAnswerId(candidateAnswerId);
        request.setScore(score);
        return createFromCandidateAnswer(request);
    }

    /**
     * Get standard answers with filtering and pagination
     */
    public Page<StandardAnswerResponse> getStandardAnswers(
            Long stdQuestionId, QuestionType type, StandardAnswerStatus status,
            int page, int size, String sort, String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<StandardAnswer> standardAnswers;

        // Apply filters
        if (stdQuestionId != null && type != null && status != null) {
            standardAnswers = standardAnswerRepository.findByStdQuestionIdAndTypeAndStatus(
                    stdQuestionId, type, status, pageable);
        } else if (stdQuestionId != null && type != null) {
            standardAnswers = standardAnswerRepository.findByStdQuestionIdAndType(
                    stdQuestionId, type, pageable);
        } else if (stdQuestionId != null && status != null) {
            standardAnswers = standardAnswerRepository.findByStdQuestionIdAndStatus(
                    stdQuestionId, status, pageable);
        } else if (type != null && status != null) {
            standardAnswers = standardAnswerRepository.findByTypeAndStatus(type, status, pageable);
        } else if (stdQuestionId != null) {
            standardAnswers = standardAnswerRepository.findByStdQuestionId(stdQuestionId, pageable);
        } else if (type != null) {
            standardAnswers = standardAnswerRepository.findByType(type, pageable);
        } else if (status != null) {
            standardAnswers = standardAnswerRepository.findByStatus(status, pageable);
        } else {
            standardAnswers = standardAnswerRepository.findAll(pageable);
        }

        return standardAnswers.map(this::convertToResponse);
    }

    /**
     * Get standard answer by ID
     */
    public StandardAnswerResponse getStandardAnswerById(Long id) {
        StandardAnswer standardAnswer = standardAnswerRepository.findWithContentById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标准答案不存在"));

        return convertToResponse(standardAnswer);
    }

    /**
     * Update standard answer
     */
    @Transactional
    public StandardAnswerResponse updateStandardAnswer(Long id, StandardAnswerUpdateRequest request) {
        StandardAnswer standardAnswer = standardAnswerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标准答案不存在"));

        // Update fields if provided
        if (request.getStatus() != null) {
            standardAnswer.setStatus(request.getStatus());
        }
        if (request.getScore() != null) {
            standardAnswer.setScore(request.getScore());
        }
        if (request.getNotes() != null) {
            standardAnswer.setNotes(request.getNotes());
        }

        standardAnswer = standardAnswerRepository.save(standardAnswer);

        log.info("Updated standard answer {} - status: {}, score: {}, notes: {}", 
                id, request.getStatus(), request.getScore(), request.getNotes());

        return convertToResponse(standardAnswer);
    }

    /**
     * Delete standard answer
     */
    @Transactional
    public void deleteStandardAnswer(Long id) {
        if (!standardAnswerRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标准答案不存在");
        }

        standardAnswerRepository.deleteById(id);
        log.info("Deleted standard answer: {}", id);
    }

    /**
     * Get standard answers for specific standard question
     */
    public List<StandardAnswerResponse> getStandardAnswersByStdQuestionId(Long stdQuestionId) {
        List<StandardAnswer> standardAnswers = standardAnswerRepository
                .findWithContentByStdQuestionId(stdQuestionId);

        return standardAnswers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find standard questions without standard answers
     */
    public List<Long> findStandardQuestionIdsWithoutAnswers(QuestionType type) {
        return standardAnswerRepository.findStandardQuestionIdsWithoutAnswers(type);
    }

    /**
     * Check if standard answer exists for candidate answer
     */
    public boolean existsBySelectedFromCandidateId(Long candidateAnswerId) {
        return standardAnswerRepository.existsBySelectedFromCandidateId(candidateAnswerId);
    }

    /**
     * Get high score answers
     */
    public List<StandardAnswerResponse> getHighScoreAnswers(Integer threshold, QuestionType type) {
        List<StandardAnswer> highScoreAnswers = standardAnswerRepository
                .findHighScoreAnswers(threshold, type);

        return highScoreAnswers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get standard answers by score range
     */
    public Page<StandardAnswerResponse> getStandardAnswersByScoreRange(
            Integer minScore, Integer maxScore, QuestionType type,
            int page, int size, String sort, String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<StandardAnswer> standardAnswers = standardAnswerRepository
                .findByScoreRange(minScore, maxScore, type, pageable);

        return standardAnswers.map(this::convertToResponse);
    }

    /**
     * Get standard answer statistics
     */
    public StandardAnswerStatisticsResponse getStatistics() {
        StandardAnswerStatisticsResponse response = new StandardAnswerStatisticsResponse();

        response.setTotalCount(standardAnswerRepository.count());

        // Status statistics
        Map<String, Long> statusStats = new HashMap<>();
        List<Object[]> statusResults = standardAnswerRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            statusStats.put(result[0].toString(), (Long) result[1]);
        }
        response.setCountByStatus(statusStats);

        // Type statistics
        Map<String, Long> typeStats = new HashMap<>();
        List<Object[]> typeResults = standardAnswerRepository.getTypeStatistics();
        for (Object[] result : typeResults) {
            typeStats.put(result[0].toString(), (Long) result[1]);
        }
        response.setCountByType(typeStats);

        // Type and status statistics
        Map<String, Long> typeStatusStats = new HashMap<>();
        List<Object[]> typeStatusResults = standardAnswerRepository.getTypeAndStatusStatistics();
        for (Object[] result : typeStatusResults) {
            String key = result[0].toString() + "_" + result[1].toString();
            typeStatusStats.put(key, (Long) result[2]);
        }
        response.setCountByTypeAndStatus(typeStatusStats);

        // Average score by type
        Map<String, Double> avgScoreStats = new HashMap<>();
        List<Object[]> avgScoreResults = standardAnswerRepository.getAverageScoreByType();
        for (Object[] result : avgScoreResults) {
            avgScoreStats.put(result[0].toString(), (Double) result[1]);
        }
        response.setAverageScoreByType(avgScoreStats);

        // Questions with answers by type
        Map<String, Long> questionsWithAnswers = new HashMap<>();
        for (QuestionType type : QuestionType.values()) {
            Long count = standardAnswerRepository.countQuestionsWithAnswersByType(type);
            questionsWithAnswers.put(type.toString(), count);
        }
        response.setQuestionsWithAnswersByType(questionsWithAnswers);

        // Coverage percentage (would need total question counts to calculate)
        Map<String, Double> coveragePercentage = new HashMap<>();
        for (QuestionType type : QuestionType.values()) {
            Long totalQuestions = standardQuestionRepository.countByType(type);
            Long questionsWithAnswersCount = questionsWithAnswers.get(type.toString());
            if (totalQuestions > 0) {
                double percentage = (questionsWithAnswersCount.doubleValue() / totalQuestions.doubleValue()) * 100;
                coveragePercentage.put(type.toString(), Math.round(percentage * 10.0) / 10.0);
            } else {
                coveragePercentage.put(type.toString(), 0.0);
            }
        }
        response.setCoveragePercentageByType(coveragePercentage);

        return response;
    }

    /**
     * Convert StandardAnswer entity to response DTO
     */
    private StandardAnswerResponse convertToResponse(StandardAnswer standardAnswer) {
        StandardAnswerResponse response = modelMapper.map(standardAnswer, StandardAnswerResponse.class);

        // Set answer content based on type
        if (standardAnswer.getType() == QuestionType.OBJECTIVE && standardAnswer.getStandardAnswerObj() != null) {
            response.setObjAnswer(standardAnswer.getStandardAnswerObj().getObjAnswer());
        } else if (standardAnswer.getType() == QuestionType.SUBJECTIVE && standardAnswer.getStandardAnswerSub() != null) {
            response.setSubAnswer(standardAnswer.getStandardAnswerSub().getSubAnswer());
        }

        return response;
    }
} 