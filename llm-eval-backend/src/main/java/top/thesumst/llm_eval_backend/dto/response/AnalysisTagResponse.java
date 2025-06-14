package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Response DTO for analysis tags
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTagResponse {

    private Long analysisTagId;
    private Long evaluationTagId;
    private Integer analysisTime;
    private String model;
    
    // Additional info from related entities
    private String evaluationTagModel;
    private Integer analysisCount; // Number of analysis results under this tag
} 