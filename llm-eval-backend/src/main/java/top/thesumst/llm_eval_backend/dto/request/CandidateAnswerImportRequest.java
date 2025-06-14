package top.thesumst.llm_eval_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;

/**
 * Request DTO for importing candidate answers
 */
@Data
@Schema(description = "Candidate answer import request")
public class CandidateAnswerImportRequest {

    @Schema(description = "Standard question ID", example = "1")
    @NotNull(message = "Standard question ID cannot be null")
    private Long stdQuestionId;

    @Schema(description = "Objective answer for objective questions", example = "A")
    private ObjectiveAnswer objAnswer;

    @Schema(description = "Subjective answer for subjective questions", example = "This is a detailed answer")
    private String subAnswer;

    @Schema(description = "Additional notes or comments", example = "Imported from CSV")
    private String notes;
} 