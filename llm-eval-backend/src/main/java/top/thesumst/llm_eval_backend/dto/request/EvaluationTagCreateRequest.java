package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for evaluation tag creation request
 */
@Data
public class EvaluationTagCreateRequest {

    @NotBlank(message = "数据集版本不能为空")
    @Size(max = 50, message = "数据集版本长度不能超过50个字符")
    private String dataSetVersion;

    @Min(value = 1, message = "评估时间必须大于0")
    private Integer evaluationTime;

    @NotBlank(message = "模型名称不能为空")
    @Size(max = 100, message = "模型名称长度不能超过100个字符")
    private String model;
} 