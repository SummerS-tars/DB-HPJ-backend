package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for adding tag to standard question request
 */
@Data
public class TagAddRequest {

    @NotBlank(message = "标签名不能为空")
    private String tagName;
} 