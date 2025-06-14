# StackOverflow Post IDs API Documentation

## Overview

This API provides functionality to retrieve post IDs of StackOverflow questions that don't have raw answers in the database. This is useful for identifying questions that need answer collection or data processing.

## API Endpoints

### 1. Get StackOverflow Post IDs Without Answers

**Endpoint:** `GET /api/v1/raw-questions/stackoverflow/post-ids-without-answers`

**Description:** Returns a list of StackOverflow post IDs for questions that don't have raw answers in the database.

**Response Format:**
```json
{
  "success": true,
  "message": "成功获取StackOverflow问题PostId列表（150个）",
  "data": {
    "postIds": [12345, 67890, 11111, 22222],
    "totalCount": 150,
    "description": "StackOverflow questions without raw answers in the database"
  },
  "timestamp": "2024-01-20T10:30:00Z"
}
```

**Example cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/stackoverflow/post-ids-without-answers" \
     -H "Content-Type: application/json"
```

### 2. Download StackOverflow Post IDs JSON File

**Endpoint:** `GET /api/v1/raw-questions/stackoverflow/post-ids-without-answers/download`

**Description:** Downloads a JSON file containing StackOverflow post IDs for questions without raw answers.

**Response Format:** Direct JSON file download
```json
{
  "postIds": [12345, 67890, 11111, 22222],
  "totalCount": 150,
  "description": "StackOverflow questions without raw answers in the database"
}
```

**Response Headers:**
- `Content-Disposition: attachment; filename="stackoverflow_raw_questions_postIds.json"`
- `Content-Type: application/json; charset=UTF-8`

**Example cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/stackoverflow/post-ids-without-answers/download" \
     -H "Accept: application/json" \
     -o "stackoverflow_raw_questions_postIds.json"
```

## Database Query Logic

The API uses the following SQL logic to identify StackOverflow questions without answers:

```sql
SELECT rq.post_id 
FROM raw_questions rq 
WHERE rq.source_platform = 'stackoverflow' 
  AND rq.post_id IS NOT NULL 
  AND NOT EXISTS (
    SELECT 1 
    FROM raw_answers ra 
    WHERE ra.raw_question_id = rq.id
  ) 
ORDER BY rq.post_id;
```

## Use Cases

### 1. Data Collection Pipeline
- Identify which StackOverflow questions need answer collection
- Prioritize questions for crawling or API calls
- Track data collection progress

### 2. Data Quality Assessment
- Monitor the completeness of question-answer pairs
- Identify gaps in the dataset
- Generate reports on data coverage

### 3. Frontend Integration
- Display statistics about missing answers
- Provide lists for manual review
- Export data for external processing

## Frontend Integration Guide

### React/JavaScript Example

```javascript
// Fetch post IDs for display
const fetchPostIds = async () => {
  try {
    const response = await fetch('/api/v1/raw-questions/stackoverflow/post-ids-without-answers');
    const result = await response.json();
    
    if (result.success) {
      console.log(`Found ${result.data.totalCount} questions without answers`);
      setPostIds(result.data.postIds);
    }
  } catch (error) {
    console.error('Failed to fetch post IDs:', error);
  }
};

// Download JSON file
const downloadPostIds = () => {
  const link = document.createElement('a');
  link.href = '/api/v1/raw-questions/stackoverflow/post-ids-without-answers/download';
  link.download = 'stackoverflow_raw_questions_postIds.json';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};
```

### Vue.js Example

```javascript
// In your Vue component
export default {
  data() {
    return {
      postIds: [],
      totalCount: 0,
      loading: false
    }
  },
  methods: {
    async fetchPostIds() {
      this.loading = true;
      try {
        const response = await this.$http.get('/api/v1/raw-questions/stackoverflow/post-ids-without-answers');
        this.postIds = response.data.data.postIds;
        this.totalCount = response.data.data.totalCount;
      } catch (error) {
        this.$message.error('Failed to load post IDs');
      } finally {
        this.loading = false;
      }
    },
    
    downloadFile() {
      window.open('/api/v1/raw-questions/stackoverflow/post-ids-without-answers/download');
    }
  }
}
```

## Error Handling

### Possible Error Responses

**500 Internal Server Error:**
```json
{
  "success": false,
  "message": "Database query failed",
  "error": "Connection timeout",
  "timestamp": "2024-01-20T10:30:00Z"
}
```

### Error Handling in Frontend

```javascript
const handleApiCall = async () => {
  try {
    const response = await fetch('/api/v1/raw-questions/stackoverflow/post-ids-without-answers');
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    
    if (!result.success) {
      throw new Error(result.message || 'API call failed');
    }
    
    return result.data;
  } catch (error) {
    console.error('API Error:', error.message);
    // Handle error appropriately in your UI
    showErrorMessage('Failed to load data. Please try again.');
  }
};
```

## Performance Considerations

### Database Optimization
- The query uses EXISTS clause for efficient checking
- Indexes on `source_platform` and `post_id` columns improve performance
- Results are ordered by `post_id` for consistent output

### Caching Strategy
- Consider caching results for frequently accessed data
- Implement cache invalidation when new answers are added
- Use Redis or similar for distributed caching

### Rate Limiting
- Implement rate limiting for API endpoints
- Consider pagination for very large result sets
- Add monitoring for API usage patterns

## Testing

### Unit Test Example

```java
@Test
void testGetStackOverflowPostIdsWithoutAnswers() {
    // Given
    List<Integer> expectedPostIds = Arrays.asList(12345, 67890);
    when(rawQuestionRepository.findStackOverflowPostIdsWithoutAnswers())
        .thenReturn(expectedPostIds);
    when(rawQuestionRepository.countStackOverflowQuestionsWithoutAnswers())
        .thenReturn(2L);
    
    // When
    StackOverflowPostIdsResponse result = rawQuestionService.getStackOverflowPostIdsWithoutAnswers();
    
    // Then
    assertThat(result.getPostIds()).isEqualTo(expectedPostIds);
    assertThat(result.getTotalCount()).isEqualTo(2L);
}
```

### Integration Test Example

```java
@Test
void testStackOverflowPostIdsEndpoint() throws Exception {
    mockMvc.perform(get("/api/v1/raw-questions/stackoverflow/post-ids-without-answers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.postIds").isArray())
        .andExpect(jsonPath("$.data.totalCount").isNumber());
}
```

## Monitoring and Logging

### Key Metrics to Monitor
- API response times
- Number of questions without answers
- Frequency of API calls
- Error rates and types

### Log Examples

```
INFO  - Getting StackOverflow post IDs without raw answers
INFO  - Found 150 StackOverflow questions without answers
INFO  - Downloading StackOverflow post IDs without raw answers as JSON file
```

## Security Considerations

### Access Control
- Ensure proper authentication if sensitive data
- Implement role-based access if needed
- Log access attempts for audit trails

### Data Protection
- Don't expose sensitive question content
- Only return post IDs (public information on StackOverflow)
- Implement CORS properly for cross-origin requests

## Deployment Checklist

- [ ] Database indexes are created for optimal performance
- [ ] API endpoints are properly documented in Swagger/OpenAPI
- [ ] Unit tests cover all service methods
- [ ] Integration tests verify end-to-end functionality
- [ ] Error handling is comprehensive
- [ ] Logging is appropriate for production monitoring
- [ ] Performance testing completed for expected load

## Future Enhancements

### Potential Improvements
1. **Pagination Support**: Add pagination for very large result sets
2. **Filtering Options**: Allow filtering by date ranges, score thresholds, etc.
3. **Real-time Updates**: WebSocket support for live updates
4. **Batch Processing**: Support for bulk operations
5. **Analytics**: Add statistical analysis of the data

### API Versioning
- Current version: v1
- Future versions should maintain backward compatibility
- Use semantic versioning for API changes 