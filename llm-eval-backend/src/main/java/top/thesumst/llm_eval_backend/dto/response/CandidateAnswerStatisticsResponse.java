package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * Response DTO for candidate answer statistics
 */
@Data
@Schema(description = "Candidate answer statistics response")
public class CandidateAnswerStatisticsResponse {

    @Schema(description = "Total count of candidate answers", example = "100")
    private Long totalCount;

    @Schema(description = "Count by status", example = "{\"PENDING\": 50, \"ACCEPTED\": 30, \"REJECTED\": 20}")
    private Map<String, Long> countByStatus;

    @Schema(description = "Count by type", example = "{\"OBJECTIVE\": 60, \"SUBJECTIVE\": 40}")
    private Map<String, Long> countByType;

    @Schema(description = "Count by type and status", example = "{\"OBJECTIVE_PENDING\": 30, \"OBJECTIVE_ACCEPTED\": 20}")
    private Map<String, Long> countByTypeAndStatus;
} 