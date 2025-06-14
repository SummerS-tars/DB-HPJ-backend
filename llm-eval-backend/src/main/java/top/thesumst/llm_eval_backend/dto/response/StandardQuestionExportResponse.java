package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO for standard question export format
 * Used for exporting questions to JSON files
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardQuestionExportResponse {

    private Long id;
    
    private String content;
} 