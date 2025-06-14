# Standard Questions and Answers Export Module Development Documentation

## Overview

This document records the complete development process for implementing the Standard Questions and Answers Export module in the LLM evaluation system. The module allows users to export standard questions along with their corresponding answers in a structured JSON format.

## Requirements Analysis

### Functional Requirements

1. **Combined Export**: Export standard questions with their corresponding answers in a single file
2. **Type Filtering**: Support filtering by question type (OBJECTIVE or SUBJECTIVE)
3. **Version-based Export**: Export based on specific dataset versions
4. **Optional Tag Filtering**: Allow filtering by tags as an optional parameter
5. **Structured Output**: Generate JSON files with specific format and naming convention
6. **Multiple Answers Support**: Handle multiple answers for subjective questions

### Technical Requirements

1. **File Format**: JSON with specific structure
2. **File Naming**: `{version}_{type}_std_q_a.json` (with optional tag)
3. **API Endpoint**: RESTful endpoint with proper HTTP headers
4. **Error Handling**: Comprehensive error responses
5. **Performance**: Efficient database queries with proper joins

## Architecture Design

### Data Flow
```
Request → Controller → Service → Repository → Database
                ↓
Response ← JSON Export ← Data Transformation ← Query Results
```

### Component Structure
```
StandardQuestionController
├── exportStandardQuestionsWithAnswers()
│
StandardQuestionService
├── exportStandardQuestionsWithAnswers()
├── generateQAExportFilename()
├── convertToQuestionAnswerPair()
└── convertToAnswerInfo()
│
StandardQuestionRepository
└── findQuestionsWithAnswersForExport()
│
StandardQuestionAnswerExportResponse (DTO)
├── QuestionAnswerPair
├── QuestionInfo
└── AnswerInfo
```

## Implementation Details

### Phase 1: DTO Creation

**File**: `StandardQuestionAnswerExportResponse.java`

**Purpose**: Define the structure for the export JSON format

**Key Features**:
- Nested static classes for clean structure
- Jackson annotations for JSON property mapping
- Support for both objective and subjective question formats

**Implementation Highlights**:
```java
@JsonProperty("q_a")
private List<QuestionAnswerPair> questionAnswerPairs;
```

### Phase 2: Repository Enhancement

**File**: `StandardQuestionRepository.java`

**Purpose**: Add efficient query method for fetching questions with answers

**Key Features**:
- Complex JOIN query with FETCH joins for performance
- Filtering by type, version, and tag
- Only includes ACCEPTED status answers
- Proper ordering by question ID

**Implementation Highlights**:
```java
@Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
       "LEFT JOIN FETCH sq.versions v " +
       "LEFT JOIN FETCH sq.tags t " +
       "LEFT JOIN FETCH sq.standardAnswers sa " +
       "LEFT JOIN FETCH sa.standardAnswerObj sao " +
       "LEFT JOIN FETCH sa.standardAnswerSub sas " +
       "WHERE (:type IS NULL OR sq.type = :type) " +
       "AND (:version IS NULL OR v.version = :version) " +
       "AND (:tag IS NULL OR t.tag = :tag) " +
       "AND sa.status = 'ACCEPTED' " +
       "ORDER BY sq.id")
```

### Phase 3: Service Layer Implementation

**File**: `StandardQuestionService.java`

**Purpose**: Business logic for export functionality

**Key Features**:
- Data validation and error handling
- JSON serialization with pretty printing
- Separate handling for objective vs subjective answers
- Filename generation with proper naming convention

**Implementation Highlights**:

1. **Main Export Method**:
```java
public String exportStandardQuestionsWithAnswers(String version, QuestionType type, String tag) {
    // Query data
    List<StandardQuestion> questions = standardQuestionRepository.findQuestionsWithAnswersForExport(type, version, tag);
    
    // Validate results
    if (questions.isEmpty()) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "未找到符合条件的标准问题和答案");
    }
    
    // Transform data
    List<QuestionAnswerPair> questionAnswerPairs = questions.stream()
            .map(this::convertToQuestionAnswerPair)
            .collect(Collectors.toList());
    
    // Create response object
    StandardQuestionAnswerExportResponse exportResponse = new StandardQuestionAnswerExportResponse();
    exportResponse.setVersion(version);
    exportResponse.setType(type.name().toLowerCase());
    exportResponse.setNumber(questionAnswerPairs.size());
    exportResponse.setQuestionAnswerPairs(questionAnswerPairs);
    
    // Serialize to JSON
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportResponse);
}
```

2. **Answer Content Extraction**:
```java
private AnswerInfo convertToAnswerInfo(StandardAnswer answer) {
    String content;
    
    if (answer.getType() == QuestionType.OBJECTIVE) {
        // For objective: A, B, C, D
        content = answer.getStandardAnswerObj() != null 
            ? answer.getStandardAnswerObj().getObjAnswer().name() 
            : "N/A";
    } else {
        // For subjective: Full text answer
        content = answer.getStandardAnswerSub() != null 
            ? answer.getStandardAnswerSub().getSubAnswer() 
            : "N/A";
    }
    
    return new AnswerInfo(answer.getId(), content);
}
```

### Phase 4: Controller Implementation

**File**: `StandardQuestionController.java`

**Purpose**: REST API endpoint for export functionality

**Key Features**:
- Proper HTTP headers for file download
- Swagger documentation
- Parameter validation
- Error response handling

**Implementation Highlights**:
```java
@GetMapping("/export-with-answers")
public ResponseEntity<String> exportStandardQuestionsWithAnswers(
        @RequestParam QuestionType type,
        @RequestParam String version,
        @RequestParam(required = false) String tag) {
    
    String jsonContent = standardQuestionService.exportStandardQuestionsWithAnswers(version, type, tag);
    String filename = standardQuestionService.generateQAExportFilename(version, type, tag);
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setContentDispositionFormData("attachment", filename);
    
    return ResponseEntity.ok()
            .headers(headers)
            .body(jsonContent);
}
```

## Database Considerations

### Query Optimization

1. **FETCH Joins**: Used to avoid N+1 query problems
2. **Filtering at Database Level**: Reduces data transfer
3. **Proper Indexing**: Recommended indexes:
   - `idx_std_questions_type_version` on (type, version)
   - `idx_std_question_tags_tag` on (tag)
   - `idx_std_answers_status` on (status)

### Data Integrity

1. **Status Filtering**: Only ACCEPTED answers are included
2. **Relationship Validation**: Proper JOIN conditions ensure data consistency
3. **NULL Handling**: Graceful handling of missing answer content

## Testing Strategy

### Unit Tests

1. **Service Layer Tests**:
   - Test data transformation logic
   - Test error handling scenarios
   - Test filename generation

2. **Repository Tests**:
   - Test query correctness
   - Test filtering combinations
   - Test performance with large datasets

### Integration Tests

1. **Controller Tests**:
   - Test HTTP response format
   - Test file download headers
   - Test parameter validation

2. **End-to-End Tests**:
   - Test complete export workflow
   - Test with real database data
   - Test file content validation

### Test Data Setup

```sql
-- Sample test data
INSERT INTO std_questions (id, type, content, status) VALUES 
(1, 'OBJECTIVE', 'Sample objective question?', 'ACCEPTED'),
(2, 'SUBJECTIVE', 'Sample subjective question?', 'ACCEPTED');

INSERT INTO std_answers (id, std_question_id, type, status) VALUES 
(101, 1, 'OBJECTIVE', 'ACCEPTED'),
(201, 2, 'SUBJECTIVE', 'ACCEPTED');

INSERT INTO std_answers_obj (std_answer_id, obj_answer) VALUES (101, 'B');
INSERT INTO std_answers_sub (std_answer_id, sub_answer) VALUES (201, 'Sample answer text');
```

## Error Handling

### Business Logic Errors

1. **No Data Found**: Returns 404 with descriptive message
2. **Invalid Parameters**: Returns 400 with validation errors
3. **Serialization Errors**: Returns 500 with technical details

### Error Response Format

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## Performance Considerations

### Database Performance

1. **Single Query Approach**: Uses complex JOIN instead of multiple queries
2. **FETCH Joins**: Reduces database round trips
3. **Result Limiting**: Consider pagination for very large exports

### Memory Management

1. **Streaming**: For large datasets, consider streaming JSON output
2. **Lazy Loading**: Proper use of JPA lazy loading
3. **Connection Pooling**: Ensure proper database connection management

### Response Time Optimization

1. **Caching**: Consider caching frequently requested exports
2. **Async Processing**: For very large exports, consider async processing
3. **Compression**: Enable GZIP compression for large JSON responses

## Security Considerations

### Data Access Control

1. **Authentication**: Standard authentication applies to endpoint
2. **Authorization**: Role-based access control if needed
3. **Data Filtering**: Only ACCEPTED answers are exposed

### Input Validation

1. **Parameter Validation**: Type, version, and tag validation
2. **SQL Injection Prevention**: Using parameterized queries
3. **XSS Prevention**: Proper HTTP headers for file downloads

## Deployment Considerations

### Configuration

1. **File Size Limits**: Configure max response size
2. **Timeout Settings**: Set appropriate request timeouts
3. **Memory Limits**: Configure JVM heap size for large exports

### Monitoring

1. **Performance Metrics**: Monitor export request times
2. **Error Tracking**: Log export failures and errors
3. **Usage Analytics**: Track export frequency and patterns

## Future Enhancements

### Functional Enhancements

1. **Multiple Format Support**: Add CSV, XML export formats
2. **Batch Export**: Support exporting multiple versions at once
3. **Scheduled Exports**: Automated periodic exports
4. **Export History**: Track and manage export history

### Technical Improvements

1. **Async Processing**: Background processing for large exports
2. **Caching Layer**: Redis cache for frequently requested exports
3. **Compression**: Built-in compression for large files
4. **Pagination**: Support for paginated large exports

### API Enhancements

1. **GraphQL Support**: Alternative query interface
2. **Webhook Integration**: Notify external systems of exports
3. **API Versioning**: Support multiple API versions
4. **Rate Limiting**: Prevent abuse of export functionality

## Conclusion

The Standard Questions and Answers Export module has been successfully implemented with the following key achievements:

1. **Complete Functionality**: Supports all required features including filtering and multiple answer types
2. **Robust Architecture**: Clean separation of concerns with proper error handling
3. **Performance Optimized**: Efficient database queries with minimal overhead
4. **Well Documented**: Comprehensive API documentation and usage examples
5. **Frontend Ready**: Complete integration examples for frontend development

The module is production-ready and provides a solid foundation for future enhancements and extensions. 