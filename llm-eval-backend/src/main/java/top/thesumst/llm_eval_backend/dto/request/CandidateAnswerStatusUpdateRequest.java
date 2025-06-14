package top.thesumst.llm_eval_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;

/**
 * Request DTO for updating candidate answer status
 */
@Data
@Schema(description = "Candidate answer status update request")
public class CandidateAnswerStatusUpdateRequest {

    @Schema(description = "New status for the candidate answer", example = "ACCEPTED")
    @NotNull(message = "Status cannot be null")
    private CandidateAnswerStatus status;

    @Schema(description = "Reason for status change", example = "Quality answer selected")
    private String reason;

    @Schema(description = "Score for the answer when accepting (0-10, optional)", example = "8")
    @Min(value = 0, message = "Score must be between 0 and 10")
    @Max(value = 10, message = "Score must be between 0 and 10")
    private Integer score;

    @Schema(description = "Whether to automatically create standard answer when accepting", example = "true")
    private Boolean createStandardAnswer = true;
} 