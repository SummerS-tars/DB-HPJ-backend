package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for evaluation analysis results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalysisResponse {

    private Long id;
    private Long evaluationResultId;
    private Long analysisTagId;
    private Integer score;
    private LocalDateTime createdAt;
    
    // Additional info from related entities
    private String analysisModel;
    private String evaluationModel;
    private Long standardQuestionId;
    private String standardQuestionContent;
} 