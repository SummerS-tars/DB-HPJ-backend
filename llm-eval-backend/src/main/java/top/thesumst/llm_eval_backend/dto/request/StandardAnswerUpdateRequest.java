package top.thesumst.llm_eval_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;

/**
 * Request DTO for updating standard answer
 */
@Data
@Schema(description = "Standard answer update request")
public class StandardAnswerUpdateRequest {

    @Schema(description = "New status for the standard answer", example = "ACCEPTED")
    private StandardAnswerStatus status;

    @Schema(description = "Updated score for the answer (0-10)", example = "9")
    @Min(value = 0, message = "Score must be between 0 and 10")
    @Max(value = 10, message = "Score must be between 0 and 10")
    private Integer score;

    @Schema(description = "Updated notes", example = "Revised score after review")
    private String notes;
} 