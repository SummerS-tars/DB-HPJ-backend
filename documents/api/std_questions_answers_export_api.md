# Standard Questions and Answers Export API

## Overview
This API allows users to export standard questions along with their corresponding answers in JSON format for specific versions, question types, and tags. This combined export is designed to facilitate evaluation processes by providing both questions and their accepted answers in a single file.

## Export Endpoint

### Export Standard Questions with Answers
Export standard questions along with their accepted answers to a downloadable JSON file.

**Endpoint:** `GET /api/v1/std-questions/export-with-answers`

**Parameters:**
- `type` (required): Question type (`OBJECTIVE` or `SUBJECTIVE`)
- `version` (required): Version filter (e.g., `v1.0`, `v1.1-beta`)
- `tag` (optional): Tag filter (e.g., `Linux`, `Security`, `SSH`)

**Response:**
- **Content-Type:** `application/json`
- **Content-Disposition:** `attachment; filename="{version}_{type}_std_q_a.json"` (with optional tag)
- **Body:** JSON object containing questions and answers

**Example Request:**
```http
GET /api/v1/std-questions/export-with-answers?type=OBJECTIVE&version=v1.0&tag=Linux
```

**Example Response Headers:**
```
Content-Type: application/json
Content-Disposition: attachment; filename="v1.0_objective_linux_std_q_a.json"
```

**Example Response Body:**
```json
{
  "version": "v1.0",
  "type": "objective",
  "number": 2,
  "q_a": [
    {
      "question": {
        "id": 1,
        "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters"
      },
      "answer": [
        {
          "id": 101,
          "content": "B"
        }
      ]
    },
    {
      "question": {
        "id": 2,
        "content": "What is the primary advantage of using SSH on port 443 instead of port 22?\nA) Better security\nB) Avoiding traffic shaping\nC) Faster connection\nD) Lower latency"
      },
      "answer": [
        {
          "id": 102,
          "content": "B"
        }
      ]
    }
  ]
}
```

**Example Response Body (Subjective Questions):**
```json
{
  "version": "v1.0",
  "type": "subjective",
  "number": 1,
  "q_a": [
    {
      "question": {
        "id": 3,
        "content": "Explain the key differences between TCP and UDP protocols and provide scenarios where each would be most appropriate."
      },
      "answer": [
        {
          "id": 201,
          "content": "TCP (Transmission Control Protocol) is a connection-oriented protocol that provides reliable, ordered delivery of data with error checking and flow control. UDP (User Datagram Protocol) is connectionless and provides faster, but unreliable data transmission without guarantees of delivery or order.\n\nTCP is most appropriate for:\n- Web browsing (HTTP/HTTPS)\n- Email transmission\n- File transfers\n- Any application requiring data integrity\n\nUDP is most appropriate for:\n- Real-time gaming\n- Video streaming\n- DNS queries\n- Applications where speed is more important than reliability"
        },
        {
          "id": 202,
          "content": "TCP ensures reliable data delivery through acknowledgments, retransmissions, and flow control, making it ideal for applications where data integrity is crucial. UDP sacrifices reliability for speed and efficiency, making it suitable for real-time applications where occasional data loss is acceptable."
        }
      ]
    }
  ]
}
```

## Response Format Details

### Root Object
- `version` (string): The dataset version used for filtering
- `type` (string): The question type in lowercase (`objective` or `subjective`)
- `number` (integer): Total number of question-answer pairs in the export
- `q_a` (array): Array of question-answer pairs

### Question-Answer Pair Object
- `question` (object): Question information
  - `id` (integer): Unique question identifier
  - `content` (string): Full question text
- `answer` (array): Array of answer objects (can contain multiple answers for subjective questions)
  - `id` (integer): Unique answer identifier
  - `content` (string): Answer content
    - For objective questions: Single letter (A, B, C, D)
    - For subjective questions: Full text answer

## Usage Examples

### Export OBJECTIVE questions for v1.0 with Linux tag:
```bash
curl -X GET "http://localhost:8080/api/v1/std-questions/export-with-answers?type=OBJECTIVE&version=v1.0&tag=Linux" \
  -H "Accept: application/json" \
  -o "v1.0_objective_linux_std_q_a.json"
```

### Export SUBJECTIVE questions for v1.1-beta with Security tag:
```bash
curl -X GET "http://localhost:8080/api/v1/std-questions/export-with-answers?type=SUBJECTIVE&version=v1.1-beta&tag=Security" \
  -H "Accept: application/json" \
  -o "v1.1-beta_subjective_security_std_q_a.json"
```

### Export all OBJECTIVE questions for v2.0 (no tag filter):
```bash
curl -X GET "http://localhost:8080/api/v1/std-questions/export-with-answers?type=OBJECTIVE&version=v2.0" \
  -H "Accept: application/json" \
  -o "v2.0_objective_std_q_a.json"
```

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "请求参数无效",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "未找到符合条件的标准问题和答案",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "导出失败: JSON serialization error",
  "data": null
}
```

## Implementation Notes

### Data Filtering
- Only questions with `ACCEPTED` status answers are included in the export
- Questions without any accepted answers are excluded from the export
- Tag filtering is optional and case-sensitive

### Multiple Answers
- Objective questions typically have one correct answer (A, B, C, or D)
- Subjective questions may have multiple accepted answers, all will be included in the export
- Each answer maintains its unique ID for traceability

### File Naming Convention
- With tag: `{version}_{type}_{tag}_std_q_a.json`
- Without tag: `{version}_{type}_std_q_a.json`
- Type is always lowercase in filename
- Examples:
  - `v1.0_objective_linux_std_q_a.json`
  - `v2.0_subjective_std_q_a.json`

## Frontend Integration

### JavaScript/React Example
```javascript
const exportQuestionsWithAnswers = async (type, version, tag = null) => {
  const params = new URLSearchParams({
    type: type,
    version: version
  });
  
  if (tag) {
    params.append('tag', tag);
  }
  
  try {
    const response = await fetch(`/api/v1/std-questions/export-with-answers?${params}`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error(`Export failed: ${response.statusText}`);
    }
    
    // Get filename from Content-Disposition header
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `${version}_${type.toLowerCase()}_std_q_a.json`;
    
    // Download file
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
  } catch (error) {
    console.error('Export failed:', error);
    throw error;
  }
};

// Usage examples
exportQuestionsWithAnswers('OBJECTIVE', 'v1.0', 'Linux');
exportQuestionsWithAnswers('SUBJECTIVE', 'v2.0');
```

### Form Component Example
```jsx
import React, { useState } from 'react';

const QuestionAnswerExportForm = () => {
  const [type, setType] = useState('OBJECTIVE');
  const [version, setVersion] = useState('');
  const [tag, setTag] = useState('');
  const [loading, setLoading] = useState(false);

  const handleExport = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await exportQuestionsWithAnswers(type, version, tag || null);
    } catch (error) {
      alert('Export failed: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleExport}>
      <div>
        <label>Question Type:</label>
        <select value={type} onChange={(e) => setType(e.target.value)} required>
          <option value="OBJECTIVE">Objective</option>
          <option value="SUBJECTIVE">Subjective</option>
        </select>
      </div>
      
      <div>
        <label>Version:</label>
        <input 
          type="text" 
          value={version} 
          onChange={(e) => setVersion(e.target.value)} 
          placeholder="e.g., v1.0"
          required 
        />
      </div>
      
      <div>
        <label>Tag (Optional):</label>
        <input 
          type="text" 
          value={tag} 
          onChange={(e) => setTag(e.target.value)} 
          placeholder="e.g., Linux, Security"
        />
      </div>
      
      <button type="submit" disabled={loading}>
        {loading ? 'Exporting...' : 'Export Questions & Answers'}
      </button>
    </form>
  );
};
```

## Performance Considerations

- Large exports may take time to process due to complex joins
- Consider implementing pagination for very large datasets
- The export includes full question content and answer text, which may result in large file sizes
- Database query optimization is implemented through proper indexing on version, type, and tag fields

## Security Considerations

- Only accepted answers are included in exports
- No sensitive internal data (like creation timestamps, user IDs) is exposed
- Standard authentication and authorization apply to the endpoint
- File downloads are served with appropriate headers to prevent XSS 