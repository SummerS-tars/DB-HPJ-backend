package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for tag creation request
 */
@Data
public class TagCreateRequest {

    @NotBlank(message = "标签名不能为空")
    @Size(max = 100, message = "标签名长度不能超过100个字符")
    private String tag;
} 