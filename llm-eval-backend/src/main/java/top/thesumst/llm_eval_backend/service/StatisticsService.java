package top.thesumst.llm_eval_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.thesumst.llm_eval_backend.dto.response.*;
import top.thesumst.llm_eval_backend.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for aggregating system-wide statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsService {

    private final RawQuestionRepository rawQuestionRepository;
    private final StandardQuestionRepository standardQuestionRepository;
    private final CandidateAnswerRepository candidateAnswerRepository;
    private final StandardAnswerRepository standardAnswerRepository;
    private final EvaluationResultRepository evaluationResultRepository;
    private final EvaluationAnalysisRepository evaluationAnalysisRepository;
    private final AnalysisTagRepository analysisTagRepository;

    /**
     * Get overall system statistics
     */
    public OverallStatisticsResponse getOverallStatistics() {
        log.info("Getting overall system statistics");

        OverallStatisticsResponse response = new OverallStatisticsResponse();

        // Raw questions statistics
        response.setRawQuestions(getRawQuestionsStats());

        // Standard questions statistics
        response.setStandardQuestions(getStandardQuestionsStats());

        // Candidate answers statistics
        response.setCandidateAnswers(getCandidateAnswersStats());

        // Standard answers statistics
        response.setStandardAnswers(getStandardAnswersStats());

        // Evaluation results statistics
        response.setEvaluationResults(getEvaluationResultsStats());

        // Analysis results statistics
        response.setAnalysisResults(getAnalysisResultsStats());

        // System metrics
        response.setSystemMetrics(getSystemMetrics(response));

        log.info("Retrieved overall system statistics successfully");
        return response;
    }

    private OverallStatisticsResponse.RawQuestionsStats getRawQuestionsStats() {
        OverallStatisticsResponse.RawQuestionsStats stats = new OverallStatisticsResponse.RawQuestionsStats();

        // Total count
        stats.setTotal(rawQuestionRepository.count());

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusResults = rawQuestionRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            byStatus.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByStatus(byStatus);

        // By platform
        Map<String, Long> byPlatform = new HashMap<>();
        List<Object[]> platformResults = rawQuestionRepository.getPlatformStatistics();
        for (Object[] result : platformResults) {
            byPlatform.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByPlatform(byPlatform);

        // Conversion rate
        long totalRaw = stats.getTotal();
        long converted = byStatus.getOrDefault("CONVERTED", 0L);
        stats.setConversionRate(totalRaw > 0 ? (double) converted / totalRaw : 0.0);

        return stats;
    }

    private OverallStatisticsResponse.StandardQuestionsStats getStandardQuestionsStats() {
        OverallStatisticsResponse.StandardQuestionsStats stats = new OverallStatisticsResponse.StandardQuestionsStats();

        // Total count
        stats.setTotal(standardQuestionRepository.count());

        // By type
        Map<String, Long> byType = new HashMap<>();
        List<Object[]> typeResults = standardQuestionRepository.getTypeStatistics();
        for (Object[] result : typeResults) {
            byType.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByType(byType);

        // By version
        Map<String, Long> byVersion = new HashMap<>();
        List<Object[]> versionResults = standardQuestionRepository.getVersionStatistics();
        for (Object[] result : versionResults) {
            String versionName = result[0] != null ? result[0].toString() : "Unknown";
            byVersion.put(versionName, (Long) result[1]);
        }
        stats.setByVersion(byVersion);

        // Questions with answers
        long withAnswers = standardQuestionRepository.countQuestionsWithAnswers();
        stats.setWithAnswers(withAnswers);

        // Answer coverage
        long total = stats.getTotal();
        stats.setAnswerCoverage(total > 0 ? (double) withAnswers / total : 0.0);

        return stats;
    }

    private OverallStatisticsResponse.CandidateAnswersStats getCandidateAnswersStats() {
        OverallStatisticsResponse.CandidateAnswersStats stats = new OverallStatisticsResponse.CandidateAnswersStats();

        // Total count
        stats.setTotal(candidateAnswerRepository.count());

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusResults = candidateAnswerRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            byStatus.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByStatus(byStatus);

        // By type
        Map<String, Long> byType = new HashMap<>();
        List<Object[]> typeResults = candidateAnswerRepository.getTypeStatistics();
        for (Object[] result : typeResults) {
            byType.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByType(byType);

        // Approval rate
        long total = stats.getTotal();
        long approved = byStatus.getOrDefault("ACCEPTED", 0L);
        stats.setApprovalRate(total > 0 ? (double) approved / total : 0.0);

        return stats;
    }

    private OverallStatisticsResponse.StandardAnswersStats getStandardAnswersStats() {
        OverallStatisticsResponse.StandardAnswersStats stats = new OverallStatisticsResponse.StandardAnswersStats();

        // Total count
        stats.setTotal(standardAnswerRepository.count());

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusResults = standardAnswerRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            byStatus.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByStatus(byStatus);

        // By type
        Map<String, Long> byType = new HashMap<>();
        List<Object[]> typeResults = standardAnswerRepository.getTypeStatistics();
        for (Object[] result : typeResults) {
            byType.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByType(byType);

        // Average score by type
        Map<String, Double> avgScoreByType = new HashMap<>();
        List<Object[]> avgScoreResults = standardAnswerRepository.getAverageScoreByType();
        for (Object[] result : avgScoreResults) {
            avgScoreByType.put(result[0].toString(), (Double) result[1]);
        }
        stats.setAverageScoreByType(avgScoreByType);

        return stats;
    }

    private OverallStatisticsResponse.EvaluationResultsStats getEvaluationResultsStats() {
        OverallStatisticsResponse.EvaluationResultsStats stats = new OverallStatisticsResponse.EvaluationResultsStats();

        // Total count
        stats.setTotal(evaluationResultRepository.count());

        // By model (from evaluation tags)
        Map<String, Long> byModel = new HashMap<>();
        List<Object[]> modelResults = evaluationResultRepository.getModelStatistics();
        for (Object[] result : modelResults) {
            byModel.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByModel(byModel);

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusResults = evaluationResultRepository.getStatusStatistics();
        for (Object[] result : statusResults) {
            byStatus.put(result[0].toString(), (Long) result[1]);
        }
        stats.setByStatus(byStatus);

        // Average score and analyzed count
        Object[] overallStats = evaluationResultRepository.getOverallStatistics();
        if (overallStats != null && overallStats.length >= 2) {
            Long totalCount = (Long) overallStats[0];
            Long analyzedCount = (Long) overallStats[1];
            stats.setAnalyzedCount(analyzedCount);
            // Note: Average score is not available from EvaluationResult, 
            // it should be calculated from EvaluationAnalysis if needed
        }

        // Get average score from EvaluationAnalysis
        Object[] analysisStats = evaluationAnalysisRepository.getOverallStatistics();
        if (analysisStats != null && analysisStats.length >= 2) {
            stats.setAverageScore((Double) analysisStats[1]); // Average score from analysis
        }

        return stats;
    }

    private OverallStatisticsResponse.AnalysisResultsStats getAnalysisResultsStats() {
        OverallStatisticsResponse.AnalysisResultsStats stats = new OverallStatisticsResponse.AnalysisResultsStats();

        // Total count
        stats.setTotal(evaluationAnalysisRepository.count());

        // Total tags
        stats.setTotalTags(analysisTagRepository.count());

        // Overall statistics
        Object[] overallStats = evaluationAnalysisRepository.getOverallStatistics();
        if (overallStats != null && overallStats.length >= 4) {
            stats.setOverallAverageScore((Double) overallStats[1]);
            stats.setMinScore((Integer) overallStats[2]);
            stats.setMaxScore((Integer) overallStats[3]);
        }

        // Score distribution
        Map<Integer, Long> scoreDistribution = new HashMap<>();
        List<Object[]> scoreResults = evaluationAnalysisRepository.getScoreDistribution();
        for (Object[] result : scoreResults) {
            scoreDistribution.put((Integer) result[0], (Long) result[1]);
        }
        stats.setScoreDistribution(scoreDistribution);

        // Average scores by model
        Map<String, Double> avgScoresByModel = new HashMap<>();
        List<Object[]> avgScoreResults = evaluationAnalysisRepository.getAverageScoresByModel();
        for (Object[] result : avgScoreResults) {
            avgScoresByModel.put(result[0].toString(), (Double) result[1]);
        }
        stats.setAverageScoresByModel(avgScoresByModel);

        // By model count
        Map<String, Long> byModel = new HashMap<>();
        List<Object[]> tagStats = evaluationAnalysisRepository.getAnalysisStatisticsByTag();
        for (Object[] result : tagStats) {
            String model = result[1].toString();
            Long count = (Long) result[2];
            byModel.merge(model, count, Long::sum);
        }
        stats.setByModel(byModel);

        return stats;
    }

    private OverallStatisticsResponse.SystemMetrics getSystemMetrics(OverallStatisticsResponse response) {
        OverallStatisticsResponse.SystemMetrics metrics = new OverallStatisticsResponse.SystemMetrics();

        // Calculate data completeness
        long totalQuestions = response.getStandardQuestions().getTotal();
        long questionsWithAnswers = response.getStandardQuestions().getWithAnswers();
        double dataCompleteness = totalQuestions > 0 ? (double) questionsWithAnswers / totalQuestions : 0.0;
        metrics.setDataCompleteness(dataCompleteness);

        // Calculate system health (based on conversion rates and data completeness)
        double conversionRate = response.getRawQuestions().getConversionRate();
        double approvalRate = response.getCandidateAnswers().getApprovalRate();
        double systemHealth = (conversionRate + approvalRate + dataCompleteness) / 3.0;
        metrics.setSystemHealth(systemHealth);

        // Total entities
        long totalEntities = response.getRawQuestions().getTotal() +
                           response.getStandardQuestions().getTotal() +
                           response.getCandidateAnswers().getTotal() +
                           response.getStandardAnswers().getTotal() +
                           response.getEvaluationResults().getTotal() +
                           response.getAnalysisResults().getTotal();
        metrics.setTotalEntities(totalEntities);

        // Last update time
        metrics.setLastUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return metrics;
    }
} 