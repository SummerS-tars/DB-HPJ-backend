package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for raw question import request
 */
@Data
public class RawQuestionImportRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 500, message = "标题长度不能超过500个字符")
    private String title;

    private String content;

    private String sourcePlatform;

    private String tags;

    private Integer postId;

    private Integer score;
} 