package top.thesumst.llm_eval_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple response DTO for post IDs - exactly matching the requested format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Simple post IDs response matching the exact requested format")
public class SimplePostIdsResponse {

    @JsonProperty("postIds")
    @Schema(description = "List of post IDs", example = "[12345, 67890, 11111]")
    private List<Integer> postIds;

    /**
     * Create response from post IDs list
     */
    public static SimplePostIdsResponse of(List<Integer> postIds) {
        return new SimplePostIdsResponse(postIds);
    }
} 