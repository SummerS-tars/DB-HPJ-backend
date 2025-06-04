package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;

/**
 * DTO for raw question response
 */
@Data
public class RawQuestionResponse {

    private Integer id;
    private String title;
    private String content;
    private String sourcePlatform;
    private String tags;
    private Integer postId;
    private Integer score;
    private RawQuestionStatus status;
} 