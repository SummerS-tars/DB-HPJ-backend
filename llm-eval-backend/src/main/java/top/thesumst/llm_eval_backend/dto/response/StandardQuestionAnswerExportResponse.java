package top.thesumst.llm_eval_backend.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for standard question and answer export format
 * Used for exporting questions with their answers to JSON files
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardQuestionAnswerExportResponse {

    private String version;
    
    private String type;
    
    private Integer number;
    
    @JsonProperty("q_a")
    private List<QuestionAnswerPair> questionAnswerPairs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionAnswerPair {
        
        private QuestionInfo question;
        
        private List<AnswerInfo> answer;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionInfo {
        
        private Long id;
        
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerInfo {
        
        private Long id;
        
        private String content;
    }
} 