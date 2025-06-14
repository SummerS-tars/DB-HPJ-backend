package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * Response DTO for standard answer statistics
 */
@Data
@Schema(description = "Standard answer statistics response")
public class StandardAnswerStatisticsResponse {

    @Schema(description = "Total number of standard answers", example = "150")
    private Long totalCount;

    @Schema(description = "Count by status", example = "{\"ACCEPTED\": 140, \"OMITTED\": 10}")
    private Map<String, Long> countByStatus;

    @Schema(description = "Count by question type", example = "{\"OBJECTIVE\": 80, \"SUBJECTIVE\": 70}")
    private Map<String, Long> countByType;

    @Schema(description = "Count by type and status combination", 
            example = "{\"OBJECTIVE_ACCEPTED\": 75, \"OBJECTIVE_OMITTED\": 5, \"SUBJECTIVE_ACCEPTED\": 65, \"SUBJECTIVE_OMITTED\": 5}")
    private Map<String, Long> countByTypeAndStatus;

    @Schema(description = "Average score by question type", example = "{\"OBJECTIVE\": 7.5, \"SUBJECTIVE\": 8.2}")
    private Map<String, Double> averageScoreByType;

    @Schema(description = "Number of questions with standard answers by type", 
            example = "{\"OBJECTIVE\": 45, \"SUBJECTIVE\": 38}")
    private Map<String, Long> questionsWithAnswersByType;

    @Schema(description = "Coverage percentage by type (questions with answers / total questions)", 
            example = "{\"OBJECTIVE\": 85.5, \"SUBJECTIVE\": 92.3}")
    private Map<String, Double> coveragePercentageByType;
} 