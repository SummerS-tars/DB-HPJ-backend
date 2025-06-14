package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.List;

/**
 * Response DTO for analysis statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisStatisticsResponse {

    private Long totalAnalysisResults;
    private Long totalAnalysisTags;
    
    // Score distribution (score -> count)
    private Map<Integer, Long> scoreDistribution;
    
    // Average scores by analysis model
    private Map<String, Double> averageScoresByModel;
    
    // Analysis results count by tag
    private List<TagAnalysisCount> tagAnalysisCounts;
    
    // Score statistics
    private Double averageScore;
    private Integer minScore;
    private Integer maxScore;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagAnalysisCount {
        private Long analysisTagId;
        private String model;
        private Long count;
        private Double averageScore;
    }
} 