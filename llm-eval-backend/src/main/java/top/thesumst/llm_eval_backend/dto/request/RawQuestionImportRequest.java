package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for raw question import request
 */
@Data
public class RawQuestionImportRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String sourcePlatform;

    private String tags;

    private Integer postId;

    private Integer score;
} 