package top.thesumst.llm_eval_backend.dto.request;

import lombok.Data;

/**
 * DTO for StackOverflow XML import request
 * Maps all relevant XML attributes
 */
@Data
public class StackOverflowXmlImportRequest {

    // Essential fields
    private String id;                // XML Id attribute
    private String postTypeId;        // "1" for questions, "2" for answers
    private String title;             // Questions only
    private String body;              // Content (needs HTML cleaning)
    private String tags;              // Questions only (needs format conversion)
    private String score;
    
    // Answer-specific fields
    private String parentId;          // For answers - links to parent question
    
    // Additional metadata (optional)
    private String creationDate;
    private String viewCount;         // Questions only
    private String ownerUserId;
    private String ownerDisplayName;
    private String lastEditDate;
    private String lastActivityDate;
    private String answerCount;       // Questions only
    private String commentCount;
    private String acceptedAnswerId;  // Questions only
    private String contentLicense;
} 