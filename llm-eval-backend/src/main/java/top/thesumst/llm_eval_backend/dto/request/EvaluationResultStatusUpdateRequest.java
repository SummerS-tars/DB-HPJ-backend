package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;

/**
 * DTO for evaluation result status update request
 */
@Data
public class EvaluationResultStatusUpdateRequest {

    @NotNull(message = "评估结果状态不能为空")
    private EvaluationResultStatus status;
} 