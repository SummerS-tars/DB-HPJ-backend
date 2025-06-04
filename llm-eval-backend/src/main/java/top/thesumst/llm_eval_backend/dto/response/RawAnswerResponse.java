package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;

/**
 * DTO for raw answer response
 */
@Data
public class RawAnswerResponse {

    private Integer id;
    private Integer rawQuestionId;
    private String content;
    private String sourcePlatform;
    private Integer postId;
    private Integer score;
} 