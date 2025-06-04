package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for raw answer import request
 */
@Data
public class RawAnswerImportRequest {

    @NotNull(message = "原始问题ID不能为空")
    private Integer rawQuestionId;

    private String content;

    private String sourcePlatform;

    private Integer postId;

    private Integer score;
} 