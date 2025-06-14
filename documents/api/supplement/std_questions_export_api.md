# Standard Questions Export API

## Overview
This API allows users to export standard questions in JSON format for specific versions, question types, and tags.

## Export Endpoint

### Export Standard Questions
Export standard questions to a downloadable JSON file.

**Endpoint:** `GET /api/v1/std-questions/export`

**Parameters:**
- `type` (required): Question type (`OBJECTIVE` or `SUBJECTIVE`)
- `version` (required): Version filter (e.g., `v1.0`, `v1.1-beta`)
- `tag` (required): Tag filter (e.g., `Linux`, `Security`, `SSH`)

**Response:**
- **Content-Type:** `application/json`
- **Content-Disposition:** `attachment; filename="{version}_{type}_{tag}.json"`
- **Body:** JSON array of standard questions

**Example Request:**
```http
GET /api/v1/std-questions/export?type=OBJECTIVE&version=v1.0&tag=Linux
```

**Example Response Headers:**
```
Content-Type: application/json
Content-Disposition: attachment; filename="v1.0_objective_linux.json"
```

**Example Response Body:**
```json
[
  {
    "id": 1,
    "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters"
  },
  {
    "id": 2,
    "content": "What is the primary advantage of using SSH on port 443 instead of port 22?\nA) Better security\nB) Avoiding traffic shaping\nC) Faster connection\nD) Lower latency"
  }
]
```

## Usage Examples

### Export OBJECTIVE questions for v1.0 with Linux tag:
```bash
curl -X GET "http://localhost:8080/api/v1/std-questions/export?type=OBJECTIVE&version=v1.0&tag=Linux" \
  -H "Accept: application/json" \
  -o "v1.0_objective_linux.json"
```

### Export SUBJECTIVE questions for v1.1-beta with Security tag:
```bash
curl -X GET "http://localhost:8080/api/v1/std-questions/export?type=SUBJECTIVE&version=v1.1-beta&tag=Security" \
  -H "Accept: application/json" \
  -o "v1.1-beta_subjective_security.json"
```

### JavaScript Frontend Example:
```javascript
// Export function
async function exportStandardQuestions(type, version, tag) {
  const params = new URLSearchParams({ type, version, tag });
  
  const response = await fetch(`/api/v1/std-questions/export?${params}`);
  
  if (response.ok) {
    const blob = await response.blob();
    const filename = response.headers.get('Content-Disposition')
      .match(/filename="(.+)"/)[1];
    
    // Trigger download
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}

// Usage
exportStandardQuestions('OBJECTIVE', 'v1.0', 'Linux');
```

## File Naming Convention
- Format: `{version}_{type}_{tag}.json`
- Question type is lowercase: `objective` or `subjective`
- Tag is lowercase

**Examples:**
- `v1.0_objective_linux.json`
- `v1.1-beta_subjective_security.json`
- `v2.0_objective_ssh.json`

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PARAMETER",
    "message": "Required parameter missing: version, type, or tag"
  }
}
```

### 404 Not Found
```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "No questions found for the specified criteria"
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

## Features
- ✅ Filter by question type (OBJECTIVE/SUBJECTIVE) - **Required**
- ✅ Filter by version - **Required**
- ✅ Filter by tag - **Required**
- ✅ Auto-generated filename with all three parameters
- ✅ Proper file download headers
- ✅ JSON format with pretty printing
- ✅ Error handling for missing required parameters
- ✅ Supports precise filtering with all three criteria

## Required Parameters Validation
All three parameters are required:
- `type`: Must be either `OBJECTIVE` or `SUBJECTIVE`
- `version`: Must be a valid version string (e.g., `v1.0`, `v1.1-beta`)
- `tag`: Must be a valid tag string (e.g., `Linux`, `Security`, `SSH`)

Missing any of these parameters will result in a 400 Bad Request error. 