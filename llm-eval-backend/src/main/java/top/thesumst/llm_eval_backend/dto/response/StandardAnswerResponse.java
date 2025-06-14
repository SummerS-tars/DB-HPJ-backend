package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for standard answer
 */
@Data
@Schema(description = "Standard answer response")
public class StandardAnswerResponse {

    @Schema(description = "Standard answer ID", example = "1")
    private Long id;

    @Schema(description = "Standard question ID", example = "5")
    private Long stdQuestionId;

    @Schema(description = "Question type", example = "SUBJECTIVE")
    private QuestionType type;

    @Schema(description = "Answer score (0-10)", example = "8")
    private Integer score;

    @Schema(description = "Answer status", example = "ACCEPTED")
    private StandardAnswerStatus status;

    @Schema(description = "Creation timestamp", example = "2024-06-14T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Source candidate answer ID", example = "123")
    private Long selectedFromCandidateId;

    @Schema(description = "Additional notes", example = "High quality answer")
    private String notes;

    // Answer content based on type
    @Schema(description = "Objective answer (for OBJECTIVE questions)", example = "A")
    private ObjectiveAnswer objAnswer;

    @Schema(description = "Subjective answer (for SUBJECTIVE questions)", example = "This is a comprehensive answer...")
    private String subAnswer;

    // Related entities (optional, for detailed responses)
    @Schema(description = "Standard question details")
    private StandardQuestionResponse standardQuestion;

    @Schema(description = "Source candidate answer details")
    private CandidateAnswerResponse sourceCandidateAnswer;
} 