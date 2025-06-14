package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for evaluation tag response
 */
@Data
public class EvaluationTagResponse {

    private Long tagId;
    private String dataSetVersion;
    private Integer evaluationTime;
    private String model;
    private LocalDateTime createdAt;
    
    // Additional fields for response
    private Long resultCount;
    private String versionName;
} 