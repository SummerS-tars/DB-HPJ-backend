package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;

import java.time.LocalDateTime;

/**
 * Response DTO for candidate answers
 */
@Data
@Schema(description = "Candidate answer response")
public class CandidateAnswerResponse {

    @Schema(description = "Candidate answer ID", example = "1")
    private Long id;

    @Schema(description = "Standard question ID", example = "1")
    private Long stdQuestionId;

    @Schema(description = "Question type", example = "OBJECTIVE")
    private QuestionType type;

    @Schema(description = "Answer status", example = "PENDING")
    private CandidateAnswerStatus status;

    @Schema(description = "Objective answer (for objective questions)", example = "A")
    private ObjectiveAnswer objAnswer;

    @Schema(description = "Subjective answer (for subjective questions)", example = "This is a detailed answer")
    private String subAnswer;

    @Schema(description = "Creation timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Standard question content", example = "What is 2+2?")
    private String questionContent;

    @Schema(description = "Standard question title", example = "Basic Math")
    private String questionTitle;
} 