package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO for raw question export format
 * Used for exporting questions to JSON files for standardization
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawQuestionExportResponse {

    private Long id;
    
    private String content;
} 