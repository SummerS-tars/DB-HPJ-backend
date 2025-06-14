package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * Response DTO for overall system statistics
 */
@Data
@Schema(description = "Overall system statistics response")
public class OverallStatisticsResponse {

    @Schema(description = "Raw questions statistics")
    private RawQuestionsStats rawQuestions;

    @Schema(description = "Standard questions statistics")
    private StandardQuestionsStats standardQuestions;

    @Schema(description = "Candidate answers statistics")
    private CandidateAnswersStats candidateAnswers;

    @Schema(description = "Standard answers statistics")
    private StandardAnswersStats standardAnswers;

    @Schema(description = "Evaluation results statistics")
    private EvaluationResultsStats evaluationResults;

    @Schema(description = "Analysis results statistics")
    private AnalysisResultsStats analysisResults;

    @Schema(description = "System performance metrics")
    private SystemMetrics systemMetrics;

    @Data
    @Schema(description = "Raw questions statistics")
    public static class RawQuestionsStats {
        private Long total;
        private Map<String, Long> byStatus;
        private Map<String, Long> byPlatform;
        private Double conversionRate;
    }

    @Data
    @Schema(description = "Standard questions statistics")
    public static class StandardQuestionsStats {
        private Long total;
        private Map<String, Long> byType;
        private Map<String, Long> byVersion;
        private Long withAnswers;
        private Double answerCoverage;
    }

    @Data
    @Schema(description = "Candidate answers statistics")
    public static class CandidateAnswersStats {
        private Long total;
        private Map<String, Long> byStatus;
        private Map<String, Long> byType;
        private Double approvalRate;
    }

    @Data
    @Schema(description = "Standard answers statistics")
    public static class StandardAnswersStats {
        private Long total;
        private Map<String, Long> byStatus;
        private Map<String, Long> byType;
        private Map<String, Double> averageScoreByType;
    }

    @Data
    @Schema(description = "Evaluation results statistics")
    public static class EvaluationResultsStats {
        private Long total;
        private Map<String, Long> byModel;
        private Map<String, Long> byStatus;
        private Double averageScore;
        private Long analyzedCount;
    }

    @Data
    @Schema(description = "Analysis results statistics")
    public static class AnalysisResultsStats {
        private Long total;
        private Long totalTags;
        private Map<String, Long> byModel;
        private Map<Integer, Long> scoreDistribution;
        private Map<String, Double> averageScoresByModel;
        private Double overallAverageScore;
        private Integer minScore;
        private Integer maxScore;
    }

    @Data
    @Schema(description = "System performance metrics")
    public static class SystemMetrics {
        private Double dataCompleteness;
        private Double systemHealth;
        private Long totalEntities;
        private String lastUpdateTime;
    }
} 