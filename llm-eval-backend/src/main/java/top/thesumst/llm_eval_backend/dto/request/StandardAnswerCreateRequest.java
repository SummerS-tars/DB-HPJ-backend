package top.thesumst.llm_eval_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating standard answer from candidate answer
 */
@Data
@Schema(description = "Standard answer creation request")
public class StandardAnswerCreateRequest {

    @Schema(description = "Source candidate answer ID", example = "123", required = true)
    @NotNull(message = "Candidate answer ID cannot be null")
    private Long candidateAnswerId;

    @Schema(description = "Score for the answer (0-10)", example = "8")
    @Min(value = 0, message = "Score must be between 0 and 10")
    @Max(value = 10, message = "Score must be between 0 and 10")
    private Integer score;

    @Schema(description = "Additional notes", example = "High quality answer selected as standard")
    private String notes;
} 