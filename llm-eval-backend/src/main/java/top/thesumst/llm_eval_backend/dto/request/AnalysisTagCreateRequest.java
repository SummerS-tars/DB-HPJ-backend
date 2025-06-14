package top.thesumst.llm_eval_backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating analysis tags
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTagCreateRequest {

    @NotNull(message = "Evaluation tag ID cannot be null")
    @Positive(message = "Evaluation tag ID must be positive")
    private Long evaluationTagId;

    @Positive(message = "Analysis time must be positive")
    private Integer analysisTime;

    @NotBlank(message = "Model name cannot be blank")
    @Size(max = 100, message = "Model name cannot exceed 100 characters")
    private String model;
} 