# Phase 7: Evaluation Analysis Module - Frontend API Documentation

## Overview
This document provides comprehensive API documentation for the Evaluation Analysis Module (Phase 7) to guide frontend development. The module provides functionality for managing analysis tags and evaluation analysis results with comprehensive statistics.

## Base URL
```
http://localhost:8080/api
```

## Authentication
All APIs use the same authentication mechanism as other modules in the system.

---

## Analysis Tag Management APIs

### 1. Create Analysis Tag
**Endpoint**: `POST /analysis-tags`

**Description**: Create a new analysis tag for a specific evaluation tag and model.

**Request Body**:
```json
{
  "evaluationTagId": 1,
  "analysisTime": 300,
  "model": "gpt-4"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "analysisTagId": 1,
    "evaluationTagId": 1,
    "analysisTime": 300,
    "model": "gpt-4",
    "evaluationTagModel": "gpt-3.5-turbo",
    "analysisCount": 0
  }
}
```

**Validation Rules**:
- `evaluationTagId`: Required, must be positive
- `analysisTime`: Optional, must be positive if provided
- `model`: Required, max 100 characters

---

### 2. Get Analysis Tag by ID
**Endpoint**: `GET /analysis-tags/{id}`

**Description**: Retrieve a specific analysis tag by its ID.

**Path Parameters**:
- `id` (Long): Analysis tag ID

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "analysisTagId": 1,
    "evaluationTagId": 1,
    "analysisTime": 300,
    "model": "gpt-4",
    "evaluationTagModel": "gpt-3.5-turbo",
    "analysisCount": 150
  }
}
```

---

### 3. Get All Analysis Tags (Paginated)
**Endpoint**: `GET /analysis-tags`

**Description**: Retrieve all analysis tags with pagination and sorting.

**Query Parameters**:
- `page` (int, default: 0): Page number (0-based)
- `size` (int, default: 20): Page size
- `sortBy` (string, default: "analysisTagId"): Sort field
- `sortDir` (string, default: "desc"): Sort direction (asc/desc)

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "analysisTagId": 1,
        "evaluationTagId": 1,
        "analysisTime": 300,
        "model": "gpt-4",
        "evaluationTagModel": "gpt-3.5-turbo",
        "analysisCount": 150
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false
      },
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

### 4. Get Analysis Tags by Evaluation Tag ID
**Endpoint**: `GET /analysis-tags/by-evaluation-tag/{evaluationTagId}`

**Description**: Retrieve all analysis tags for a specific evaluation tag.

**Path Parameters**:
- `evaluationTagId` (Long): Evaluation tag ID

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "analysisTagId": 1,
      "evaluationTagId": 1,
      "analysisTime": 300,
      "model": "gpt-4",
      "evaluationTagModel": "gpt-3.5-turbo",
      "analysisCount": 150
    }
  ]
}
```

---

### 5. Get Analysis Tags by Model
**Endpoint**: `GET /analysis-tags/by-model`

**Description**: Retrieve all analysis tags for a specific model.

**Query Parameters**:
- `model` (string): Model name

**Response**: Same as above

---

### 6. Update Analysis Tag
**Endpoint**: `PUT /analysis-tags/{id}`

**Description**: Update an existing analysis tag.

**Path Parameters**:
- `id` (Long): Analysis tag ID

**Request Body**: Same as create request

**Response**: Same as create response

---

### 7. Delete Analysis Tag
**Endpoint**: `DELETE /analysis-tags/{id}`

**Description**: Delete an analysis tag and all its associated analysis results.

**Path Parameters**:
- `id` (Long): Analysis tag ID

**Response**:
```json
{
  "success": true,
  "message": "Analysis tag deleted successfully",
  "data": null
}
```

---

## Evaluation Analysis Management APIs

### 1. Import Analysis Results
**Endpoint**: `POST /evaluation-analysis/import`

**Description**: Import evaluation analysis results in batch.

**Request Body**:
```json
{
  "analysisTagId": 1,
  "results": [
    {
      "evaluationResultId": 1,
      "score": 8
    },
    {
      "evaluationResultId": 2,
      "score": 7
    }
  ]
}
```

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "message": "Import completed - Success: 2, Skip: 0, Error: 0. ",
    "importedCount": 2,
    "failedCount": 0,
    "errors": null
  }
}
```

**Validation Rules**:
- `analysisTagId`: Required, must be positive
- `results`: Required, cannot be empty
- `evaluationResultId`: Required, must be positive
- `score`: Required, must be between 0 and 10

---

### 2. Get Analysis Result by ID
**Endpoint**: `GET /evaluation-analysis/{id}`

**Description**: Retrieve a specific analysis result by its ID.

**Path Parameters**:
- `id` (Long): Analysis result ID

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "evaluationResultId": 1,
    "analysisTagId": 1,
    "score": 8,
    "createdAt": "2024-01-15T10:30:00",
    "analysisModel": "gpt-4",
    "evaluationModel": "gpt-3.5-turbo",
    "standardQuestionId": 1,
    "standardQuestionTitle": "Sample Question"
  }
}
```

---

### 3. Get All Analysis Results (Paginated)
**Endpoint**: `GET /evaluation-analysis`

**Description**: Retrieve all analysis results with pagination.

**Query Parameters**:
- `page` (int, default: 0): Page number
- `size` (int, default: 20): Page size
- `sortBy` (string, default: "id"): Sort field
- `sortDir` (string, default: "desc"): Sort direction

**Response**: Paginated list of analysis results

---

### 4. Get Analysis Results by Analysis Tag ID
**Endpoint**: `GET /evaluation-analysis/by-tag/{analysisTagId}`

**Description**: Retrieve analysis results for a specific analysis tag.

**Path Parameters**:
- `analysisTagId` (Long): Analysis tag ID

**Query Parameters**: Same pagination parameters as above

**Response**: Paginated list of detailed analysis results with additional information

---

### 5. Get Overall Analysis Statistics
**Endpoint**: `GET /evaluation-analysis/statistics`

**Description**: Retrieve comprehensive analysis statistics.

**Response**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalAnalysisResults": 1000,
    "totalAnalysisTags": 5,
    "scoreDistribution": {
      "0": 10,
      "1": 15,
      "2": 25,
      "3": 40,
      "4": 60,
      "5": 100,
      "6": 150,
      "7": 200,
      "8": 250,
      "9": 120,
      "10": 30
    },
    "averageScoresByModel": {
      "gpt-4": 7.8,
      "gpt-3.5-turbo": 6.5,
      "claude-3": 7.2
    },
    "tagAnalysisCounts": [
      {
        "analysisTagId": 1,
        "model": "gpt-4",
        "count": 300,
        "averageScore": 7.8
      }
    ],
    "averageScore": 7.1,
    "minScore": 0,
    "maxScore": 10
  }
}
```

---

### 6. Get Analysis Statistics by Analysis Tag ID
**Endpoint**: `GET /evaluation-analysis/statistics/by-tag/{analysisTagId}`

**Description**: Retrieve analysis statistics for a specific analysis tag.

**Path Parameters**:
- `analysisTagId` (Long): Analysis tag ID

**Response**: Similar to overall statistics but filtered for the specific tag

---

### 7. Delete Analysis Result
**Endpoint**: `DELETE /evaluation-analysis/{id}`

**Description**: Delete a specific analysis result.

**Path Parameters**:
- `id` (Long): Analysis result ID

**Response**:
```json
{
  "success": true,
  "message": "Analysis result deleted successfully",
  "data": null
}
```

---

## Error Handling

### Common Error Responses

**Resource Not Found (404)**:
```json
{
  "success": false,
  "message": "Analysis tag not found with ID: 999",
  "data": null
}
```

**Validation Error (400)**:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "evaluationTagId": "Evaluation tag ID cannot be null",
    "model": "Model name cannot be blank"
  }
}
```

**Duplicate Resource (409)**:
```json
{
  "success": false,
  "message": "Analysis tag already exists for evaluation tag ID: 1 and model: gpt-4",
  "data": null
}
```

---

## Frontend Integration Guidelines

### 1. Analysis Tag Management UI
- **Create Form**: Include evaluation tag selection, model input, and optional analysis time
- **List View**: Display analysis tags with pagination, sorting, and filtering by model/evaluation tag
- **Edit Form**: Allow updating analysis tag properties
- **Delete Confirmation**: Warn users that deleting will also remove all associated analysis results

### 2. Analysis Results Import UI
- **Batch Import**: Support CSV/JSON file upload or manual entry
- **Progress Indicator**: Show import progress and results
- **Error Handling**: Display detailed error messages for failed imports
- **Validation**: Client-side validation for score ranges (0-10)

### 3. Statistics Dashboard
- **Score Distribution Chart**: Bar chart or histogram showing score distribution
- **Model Comparison**: Comparative charts showing average scores by model
- **Tag Performance**: Table or chart showing analysis counts and averages by tag
- **Filters**: Allow filtering statistics by date range, model, or evaluation tag

### 4. Analysis Results Viewer
- **Detailed View**: Show analysis results with related question and evaluation information
- **Filtering**: Filter by score range, model, date, etc.
- **Export**: Allow exporting analysis results to CSV/Excel

### 5. State Management Recommendations
```javascript
// Example state structure
const analysisState = {
  analysisTags: {
    items: [],
    loading: false,
    pagination: { page: 0, size: 20, total: 0 }
  },
  analysisResults: {
    items: [],
    loading: false,
    pagination: { page: 0, size: 20, total: 0 }
  },
  statistics: {
    overall: null,
    byTag: {},
    loading: false
  }
};
```

### 6. API Call Examples
```javascript
// Create analysis tag
const createAnalysisTag = async (data) => {
  const response = await fetch('/api/analysis-tags', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return response.json();
};

// Import analysis results
const importAnalysisResults = async (data) => {
  const response = await fetch('/api/evaluation-analysis/import', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return response.json();
};

// Get statistics
const getStatistics = async () => {
  const response = await fetch('/api/evaluation-analysis/statistics');
  return response.json();
};
```

---

## Data Flow Diagram

```
Frontend → Analysis Tag APIs → Analysis Tag Management
    ↓
Analysis Results Import → Batch Processing → Database
    ↓
Statistics APIs → Data Aggregation → Dashboard Display
```

## Notes for Frontend Developers

1. **Pagination**: All list APIs support pagination. Always handle pagination state properly.
2. **Error Handling**: Implement comprehensive error handling for all API calls.
3. **Loading States**: Show loading indicators during API calls.
4. **Validation**: Implement client-side validation matching server-side rules.
5. **Real-time Updates**: Consider implementing WebSocket or polling for real-time statistics updates.
6. **Performance**: Use debouncing for search/filter inputs to avoid excessive API calls.
7. **Caching**: Consider caching statistics data as it may be expensive to compute.

## Testing Recommendations

1. **Unit Tests**: Test all API integration functions
2. **Integration Tests**: Test complete workflows (create tag → import results → view statistics)
3. **Error Scenarios**: Test error handling for various failure cases
4. **Performance Tests**: Test with large datasets for pagination and statistics
5. **User Experience**: Test loading states, error messages, and success feedback 