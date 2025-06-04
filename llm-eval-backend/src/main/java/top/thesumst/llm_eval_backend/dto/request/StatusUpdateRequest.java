package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;

/**
 * DTO for status update request
 */
@Data
public class StatusUpdateRequest {

    @NotNull(message = "状态不能为空")
    private RawQuestionStatus status;
} 