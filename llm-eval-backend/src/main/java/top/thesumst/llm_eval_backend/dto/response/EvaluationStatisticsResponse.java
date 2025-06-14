package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import java.util.Map;

/**
 * DTO for evaluation statistics response
 */
@Data
public class EvaluationStatisticsResponse {

    private Long tagId;
    private String model;
    private String dataSetVersion;
    
    // Statistics by status
    private Long totalResults;
    private Long pendingResults;
    private Long analyzedResults;
    private Long omittedResults;
    
    // Statistics by type
    private Long objectiveResults;
    private Long subjectiveResults;
    
    // Additional statistics
    private Map<String, Long> statusCounts;
    private Map<String, Long> typeCounts;
    private Double averageScore;
    private Integer totalQuestions;
    private Integer analyzedQuestions;
} 