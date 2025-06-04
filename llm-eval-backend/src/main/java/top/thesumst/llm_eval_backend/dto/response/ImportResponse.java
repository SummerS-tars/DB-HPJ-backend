package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO for import operation response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {

    private String message;
    private Integer importedCount;
    private Integer failedCount;
    private List<ImportError> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private String originalRecord;
        private String error;
    }
} 