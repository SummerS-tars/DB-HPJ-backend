package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;

/**
 * DTO for tag response
 */
@Data
public class TagResponse {

    private String tag;
    
    private Long questionCount; // Number of standard questions with this tag
} 