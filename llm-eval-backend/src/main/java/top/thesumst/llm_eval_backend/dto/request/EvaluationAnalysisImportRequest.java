package top.thesumst.llm_eval_backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Request DTO for importing evaluation analysis results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalysisImportRequest {

    @NotNull(message = "Analysis tag ID cannot be null")
    @Positive(message = "Analysis tag ID must be positive")
    private Long analysisTagId;

    @NotEmpty(message = "Analysis results cannot be empty")
    private List<AnalysisResultItem> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResultItem {
        
        @NotNull(message = "Evaluation result ID cannot be null")
        @Positive(message = "Evaluation result ID must be positive")
        private Long evaluationResultId;

        @NotNull(message = "Score cannot be null")
        @Min(value = 0, message = "Score must be at least 0")
        @Max(value = 10, message = "Score must be at most 10")
        private Integer score;
    }
} 