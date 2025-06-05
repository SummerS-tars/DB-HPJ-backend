package top.thesumst.llm_eval_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;
import lombok.Data;

import java.util.List;

/**
 * DTO for standard question import request
 */
@Data
public class StandardQuestionImportRequest {

    @NotNull(message = "原始问题ID不能为空")
    private Long originalRawQuestionId;

    @NotNull(message = "问题类型不能为空")
    private QuestionType type;

    @NotBlank(message = "问题内容不能为空")
    private String content;

    private StandardQuestionStatus status = StandardQuestionStatus.WAITING_ANSWERS;

    private List<String> versionIds;

    private List<String> tagNames;
} 