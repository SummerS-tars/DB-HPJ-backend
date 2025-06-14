# Raw Questions Export API

## Overview
This API allows users to export raw questions in JSON format for standardization processing. The export can be customized to include or exclude questions with 'CONVERTED' status and limit the number of questions exported.

## Export Endpoint

### Export Raw Questions for Standardization
Export raw questions to a downloadable JSON file for standardization processing.

**Endpoint:** `GET /api/v1/raw-questions/export`

**Parameters:**
- `includeConverted` (optional): Boolean flag to include questions with 'CONVERTED' status (default: `false`)
- `limit` (optional): Integer to limit the number of questions exported (default: export all questions)

**Response:**
- **Content-Type:** `application/json`
- **Content-Disposition:** `attachment; filename="raw_questions_for_standardize.json"`
- **Body:** JSON array of raw questions with id and content

**Example Request:**
```http
GET /api/v1/raw-questions/export
```

**Example Request with Parameters:**
```http
GET /api/v1/raw-questions/export?includeConverted=false&limit=100
```

**Example Response Headers:**
```
Content-Type: application/json
Content-Disposition: attachment; filename="raw_questions_for_standardize.json"
```

**Example Response Body:**
```json
[
  {
    "id": 2,
    "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters"
  },
  {
    "id": 5,  
    "content": "How do you configure SSH to use port 443 instead of the default port 22? What are the security considerations when making this change?"
  }
]
```

## Usage Examples

### Export all questions with default settings (exclude CONVERTED):
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/export" \
  -H "Accept: application/json" \
  -o "raw_questions_for_standardize.json"
```

### Export including CONVERTED questions:
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/export?includeConverted=true" \
  -H "Accept: application/json" \
  -o "raw_questions_for_standardize.json"
```

### Export limited number of questions:
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/export?limit=50" \
  -H "Accept: application/json" \
  -o "raw_questions_for_standardize.json"
```

### Export with both parameters:
```bash
curl -X GET "http://localhost:8080/api/v1/raw-questions/export?includeConverted=true&limit=100" \
  -H "Accept: application/json" \
  -o "raw_questions_for_standardize.json"
```

### JavaScript Frontend Example:
```javascript
// Export function with options
async function exportRawQuestions(includeConverted = false, limit = null) {
  const params = new URLSearchParams();
  
  if (includeConverted) {
    params.append('includeConverted', 'true');
  }
  
  if (limit && limit > 0) {
    params.append('limit', limit.toString());
  }
  
  const url = `/api/v1/raw-questions/export${params.toString() ? '?' + params.toString() : ''}`;
  
  const response = await fetch(url);
  
  if (response.ok) {
    const blob = await response.blob();
    const filename = response.headers.get('Content-Disposition')
      .match(/filename="(.+)"/)[1];
    
    // Trigger download
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(downloadUrl);
  } else {
    console.error('Export failed:', response.statusText);
  }
}

// Usage examples
exportRawQuestions(); // Default: exclude CONVERTED, export all
exportRawQuestions(true); // Include CONVERTED, export all  
exportRawQuestions(false, 50); // Exclude CONVERTED, limit to 50
exportRawQuestions(true, 100); // Include CONVERTED, limit to 100
```

## File Structure

### Fixed File Name
The exported file always uses the fixed filename: `raw_questions_for_standardize.json`

### JSON Format
Each question object contains:
- `id`: Long - Unique identifier of the raw question
- `content`: String - The content of the question (uses `content` field if available, otherwise falls back to `title`)

## Filtering Logic

### Status Filtering
- **By default:** Only exports questions with status `WAITING_CONVERTED`
- **With `includeConverted=true`:** Exports questions with both `WAITING_CONVERTED` and `CONVERTED` statuses
- **Excluded statuses:** Questions with other statuses (e.g., `REJECTED`, `DRAFT`) are never included

### Quantity Limiting
- **Default behavior:** Exports all qualifying questions
- **With `limit` parameter:** Exports up to the specified number of questions
- **Ordering:** Questions are exported in ascending order by ID for consistency

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PARAMETER",
    "message": "Invalid limit parameter: must be a positive integer"
  }
}
```

### 404 Not Found
```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "No raw questions found for export"
  }
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "Export failed: JSON serialization error"
  }
}
```

## Features Summary
- ✅ **Default Behavior:** Export only `WAITING_CONVERTED` questions
- ✅ **Optional Include CONVERTED:** Add `includeConverted=true` to include converted questions
- ✅ **Quantity Control:** Use `limit` parameter to restrict export size
- ✅ **Fixed Filename:** Always exports as `raw_questions_for_standardize.json`
- ✅ **Content Priority:** Uses question `content` field, falls back to `title` if content is empty
- ✅ **Consistent Ordering:** Results ordered by ID ascending
- ✅ **Error Handling:** Comprehensive error responses for different failure scenarios
- ✅ **Download Support:** Proper headers for browser file download

## Integration Notes

### Frontend Integration
- The API is designed for direct browser download integration
- Response includes proper `Content-Disposition` header for file downloads
- JSON format is suitable for further processing in standardization workflows

### Data Processing
- Export format matches the specification in the requirements document
- Questions are pre-filtered by status to focus on standardization candidates  
- Consistent ID ordering ensures reproducible exports for batch processing

### Performance Considerations  
- Large exports (without `limit`) may take time for databases with many questions
- Consider using pagination in frontend UI for better user experience
- Export operations are logged for monitoring and debugging purposes 