package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;

/**
 * DTO for evaluation result import request
 */
@Data
public class EvaluationResultImportRequest {

    @NotNull(message = "评估标签ID不能为空")
    private Long evaluationTagId;

    @NotNull(message = "标准问题ID不能为空")
    private Long stdQuestionId;

    @NotBlank(message = "评估内容不能为空")
    private String content;

    @NotNull(message = "问题类型不能为空")
    private QuestionType type;

    private EvaluationResultStatus status = EvaluationResultStatus.PENDING;
} 