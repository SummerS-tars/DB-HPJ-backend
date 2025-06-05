package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for version creation request
 */
@Data
public class VersionCreateRequest {

    @NotBlank(message = "版本号不能为空")
    @Size(max = 20, message = "版本号长度不能超过20个字符")
    private String version;
} 