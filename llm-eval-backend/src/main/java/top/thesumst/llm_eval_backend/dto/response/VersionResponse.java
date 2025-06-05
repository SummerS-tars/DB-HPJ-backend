package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for version response
 */
@Data
public class VersionResponse {

    private String version;
    
    private LocalDateTime createdAt;
    
    private Long questionCount; // Number of standard questions in this version
} 