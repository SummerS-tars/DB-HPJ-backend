package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for standard question response
 */
@Data
public class StandardQuestionResponse {

    private Long id;
    
    private Long originalRawQuestionId;
    
    private QuestionType type;
    
    private String content;
    
    private StandardQuestionStatus status;
    
    private LocalDateTime createdAt;
    
    private List<VersionResponse> versions;
    
    private List<TagResponse> tags;
    
    private RawQuestionResponse originalRawQuestion; // Related original question info
} 