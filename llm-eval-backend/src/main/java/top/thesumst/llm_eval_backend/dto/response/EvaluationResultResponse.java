package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;

/**
 * DTO for evaluation result response
 */
@Data
public class EvaluationResultResponse {

    private Long id;
    private Long evaluationTagId;
    private Long stdQuestionId;
    private String content;
    private QuestionType type;
    private EvaluationResultStatus status;
    
    // Related entity information
    private String model;
    private String dataSetVersion;
    private String questionTitle;
    private String questionContent;
    
    // Analysis information
    private Long analysisCount;
    private Double averageScore;
} 