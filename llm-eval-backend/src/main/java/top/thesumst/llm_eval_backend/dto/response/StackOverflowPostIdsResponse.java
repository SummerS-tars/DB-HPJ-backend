package top.thesumst.llm_eval_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for StackOverflow post IDs without raw answers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "StackOverflow post IDs response")
public class StackOverflowPostIdsResponse {

    @Schema(description = "List of StackOverflow post IDs that don't have raw answers", 
            example = "[12345, 67890, 11111]")
    private List<Integer> postIds;

    @Schema(description = "Total count of questions without answers", example = "150")
    private long totalCount;

    @Schema(description = "Description of the data", 
            example = "StackOverflow questions without raw answers in the database")
    private String description;

    /**
     * Create response with post IDs only
     */
    public static StackOverflowPostIdsResponse of(List<Integer> postIds) {
        return new StackOverflowPostIdsResponse(
            postIds,
            postIds.size(),
            "StackOverflow questions without raw answers in the database"
        );
    }

    /**
     * Create response with post IDs and custom count
     */
    public static StackOverflowPostIdsResponse of(List<Integer> postIds, long totalCount) {
        return new StackOverflowPostIdsResponse(
            postIds,
            totalCount,
            "StackOverflow questions without raw answers in the database"
        );
    }
} 