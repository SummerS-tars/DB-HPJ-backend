package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.request.EvaluationAnalysisImportRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationAnalysisResponse;
import top.thesumst.llm_eval_backend.dto.response.AnalysisStatisticsResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.entity.EvaluationAnalysis;
import top.thesumst.llm_eval_backend.entity.AnalysisTag;
import top.thesumst.llm_eval_backend.entity.EvaluationResult;
import top.thesumst.llm_eval_backend.exception.BusinessException;
import top.thesumst.llm_eval_backend.exception.ErrorCode;
import top.thesumst.llm_eval_backend.repository.EvaluationAnalysisRepository;
import top.thesumst.llm_eval_backend.repository.AnalysisTagRepository;
import top.thesumst.llm_eval_backend.repository.EvaluationResultRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Optional;

/**
 * Service class for EvaluationAnalysis operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationAnalysisService {

    private final EvaluationAnalysisRepository evaluationAnalysisRepository;
    private final AnalysisTagRepository analysisTagRepository;
    private final EvaluationResultRepository evaluationResultRepository;
    private final ModelMapper modelMapper;

    /**
     * Import evaluation analysis results
     */
    public ImportResponse importAnalysisResults(EvaluationAnalysisImportRequest request) {
        log.info("Importing analysis results for analysis tag ID: {}, count: {}", 
                request.getAnalysisTagId(), request.getResults().size());

        // Check if analysis tag exists
        AnalysisTag analysisTag = analysisTagRepository.findById(request.getAnalysisTagId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "Analysis tag not found with ID: " + request.getAnalysisTagId()));

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (EvaluationAnalysisImportRequest.AnalysisResultItem item : request.getResults()) {
            try {
                // Check if evaluation result exists
                if (!evaluationResultRepository.existsById(item.getEvaluationResultId())) {
                    errorCount++;
                    errorMessages.append("Evaluation result not found with ID: ")
                            .append(item.getEvaluationResultId()).append("; ");
                    continue;
                }

                // Check if analysis result already exists
                if (evaluationAnalysisRepository.existsByEvaluationResultIdAndAnalysisTagId(
                        item.getEvaluationResultId(), request.getAnalysisTagId())) {
                    skipCount++;
                    continue;
                }

                // Create analysis result
                EvaluationAnalysis analysis = new EvaluationAnalysis();
                analysis.setEvaluationResultId(item.getEvaluationResultId());
                analysis.setAnalysisTagId(request.getAnalysisTagId());
                analysis.setScore(item.getScore());
                analysis.setCreatedAt(LocalDateTime.now());

                evaluationAnalysisRepository.save(analysis);
                successCount++;

            } catch (Exception e) {
                errorCount++;
                errorMessages.append("Error processing evaluation result ID ")
                        .append(item.getEvaluationResultId()).append(": ")
                        .append(e.getMessage()).append("; ");
                log.error("Error importing analysis result for evaluation result ID: {}", 
                        item.getEvaluationResultId(), e);
            }
        }

        log.info("Import completed - Success: {}, Skip: {}, Error: {}", successCount, skipCount, errorCount);

        ImportResponse response = new ImportResponse();
        response.setImportedCount(successCount);
        response.setFailedCount(errorCount);
        response.setMessage(String.format("Import completed - Success: %d, Skip: %d, Error: %d. %s", 
                successCount, skipCount, errorCount, 
                errorMessages.length() > 0 ? "Errors: " + errorMessages.toString() : ""));

        return response;
    }

    /**
     * Get analysis result by ID
     */
    @Transactional(readOnly = true)
    public EvaluationAnalysisResponse getAnalysisResultById(Long id) {
        log.info("Getting analysis result by ID: {}", id);

        Optional<Object[]> analysisDetails = evaluationAnalysisRepository.findAnalysisResultWithDetailsById(id);
        
        if (analysisDetails.isPresent()) {
            return convertDetailedResultToResponse(analysisDetails.get());
        } else {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Analysis result not found with ID: " + id);
        }
    }

    /**
     * Get analysis results by analysis tag ID
     */
    @Transactional(readOnly = true)
    public Page<EvaluationAnalysisResponse> getAnalysisResultsByTagId(Long analysisTagId, Pageable pageable) {
        log.info("Getting analysis results by analysis tag ID: {}", analysisTagId);

        // Check if analysis tag exists
        if (!analysisTagRepository.existsById(analysisTagId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Analysis tag not found with ID: " + analysisTagId);
        }

        Page<Object[]> resultsPage = evaluationAnalysisRepository.findAnalysisResultsWithDetails(analysisTagId, pageable);
        
        return resultsPage.map(this::convertDetailedResultToResponse);
    }

    /**
     * Get all analysis results with pagination
     */
    @Transactional(readOnly = true)
    public Page<EvaluationAnalysisResponse> getAllAnalysisResults(Pageable pageable) {
        log.info("Getting all analysis results with pagination");

        Page<Object[]> analysisPage = evaluationAnalysisRepository.findAllAnalysisResultsWithDetails(pageable);
        
        return analysisPage.map(this::convertDetailedResultToResponse);
    }

    /**
     * Get analysis statistics
     */
    @Transactional(readOnly = true)
    public AnalysisStatisticsResponse getAnalysisStatistics() {
        log.info("Getting analysis statistics");

        AnalysisStatisticsResponse response = new AnalysisStatisticsResponse();

        // Get overall statistics
        Object[] overallStats = evaluationAnalysisRepository.getOverallStatistics();
        if (overallStats != null && overallStats.length >= 4) {
            response.setTotalAnalysisResults((Long) overallStats[0]);
            response.setAverageScore((Double) overallStats[1]);
            response.setMinScore((Integer) overallStats[2]);
            response.setMaxScore((Integer) overallStats[3]);
        }

        // Get total analysis tags count
        response.setTotalAnalysisTags(analysisTagRepository.count());

        // Get score distribution
        List<Object[]> scoreDistribution = evaluationAnalysisRepository.getScoreDistribution();
        Map<Integer, Long> scoreDistMap = new HashMap<>();
        for (Object[] row : scoreDistribution) {
            scoreDistMap.put((Integer) row[0], (Long) row[1]);
        }
        response.setScoreDistribution(scoreDistMap);

        // Get average scores by model
        List<Object[]> avgScoresByModel = evaluationAnalysisRepository.getAverageScoresByModel();
        Map<String, Double> avgScoresMap = new HashMap<>();
        for (Object[] row : avgScoresByModel) {
            avgScoresMap.put((String) row[0], (Double) row[1]);
        }
        response.setAverageScoresByModel(avgScoresMap);

        // Get analysis statistics by tag
        List<Object[]> tagStats = evaluationAnalysisRepository.getAnalysisStatisticsByTag();
        List<AnalysisStatisticsResponse.TagAnalysisCount> tagAnalysisCounts = tagStats.stream()
                .map(row -> new AnalysisStatisticsResponse.TagAnalysisCount(
                        (Long) row[0],    // analysisTagId
                        (String) row[1],  // model
                        (Long) row[2],    // count
                        (Double) row[3]   // averageScore
                ))
                .collect(Collectors.toList());
        response.setTagAnalysisCounts(tagAnalysisCounts);

        return response;
    }

    /**
     * Get analysis statistics by analysis tag ID
     */
    @Transactional(readOnly = true)
    public AnalysisStatisticsResponse getAnalysisStatisticsByTagId(Long analysisTagId) {
        log.info("Getting analysis statistics by analysis tag ID: {}", analysisTagId);

        // Check if analysis tag exists
        if (!analysisTagRepository.existsById(analysisTagId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Analysis tag not found with ID: " + analysisTagId);
        }

        AnalysisStatisticsResponse response = new AnalysisStatisticsResponse();

        // Get analysis results count for this tag
        long analysisCount = evaluationAnalysisRepository.countByAnalysisTagId(analysisTagId);
        response.setTotalAnalysisResults(analysisCount);

        // Get score distribution for this tag
        List<Object[]> scoreDistribution = evaluationAnalysisRepository.getScoreDistributionByAnalysisTag(analysisTagId);
        Map<Integer, Long> scoreDistMap = new HashMap<>();
        for (Object[] row : scoreDistribution) {
            scoreDistMap.put((Integer) row[0], (Long) row[1]);
        }
        response.setScoreDistribution(scoreDistMap);

        // Calculate statistics from score distribution
        if (!scoreDistMap.isEmpty()) {
            double totalScore = 0;
            long totalCount = 0;
            int minScore = Integer.MAX_VALUE;
            int maxScore = Integer.MIN_VALUE;

            for (Map.Entry<Integer, Long> entry : scoreDistMap.entrySet()) {
                int score = entry.getKey();
                long count = entry.getValue();
                
                totalScore += score * count;
                totalCount += count;
                minScore = Math.min(minScore, score);
                maxScore = Math.max(maxScore, score);
            }

            response.setAverageScore(totalCount > 0 ? totalScore / totalCount : 0.0);
            response.setMinScore(minScore != Integer.MAX_VALUE ? minScore : null);
            response.setMaxScore(maxScore != Integer.MIN_VALUE ? maxScore : null);
        }

        return response;
    }

    /**
     * Delete analysis result
     */
    public void deleteAnalysisResult(Long id) {
        log.info("Deleting analysis result with ID: {}", id);

        if (!evaluationAnalysisRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Analysis result not found with ID: " + id);
        }

        evaluationAnalysisRepository.deleteById(id);
        log.info("Deleted analysis result with ID: {}", id);
    }

    /**
     * Convert EvaluationAnalysis entity to response DTO
     */
    private EvaluationAnalysisResponse convertToResponse(EvaluationAnalysis analysis) {
        EvaluationAnalysisResponse response = modelMapper.map(analysis, EvaluationAnalysisResponse.class);
        
        // Add additional information if needed
        // This could be enhanced to include related entity information
        
        return response;
    }

    /**
     * Convert detailed query result to response DTO
     */
    private EvaluationAnalysisResponse convertDetailedResultToResponse(Object[] row) {
        EvaluationAnalysis analysis = (EvaluationAnalysis) row[0];
        String analysisModel = (String) row[1];
        String evaluationModel = (String) row[2];
        Long standardQuestionId = (Long) row[3];
        String standardQuestionContent = (String) row[4];

        EvaluationAnalysisResponse response = modelMapper.map(analysis, EvaluationAnalysisResponse.class);
        response.setAnalysisModel(analysisModel);
        response.setEvaluationModel(evaluationModel);
        response.setStandardQuestionId(standardQuestionId);
        response.setStandardQuestionContent(standardQuestionContent);

        return response;
    }
} 