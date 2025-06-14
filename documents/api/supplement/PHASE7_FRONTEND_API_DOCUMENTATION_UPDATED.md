# Phase 7: Evaluation Analysis Module - Updated Frontend API Documentation

## Overview
This document provides the **corrected and updated** API documentation for the Evaluation Analysis Module (Phase 7) after all fixes have been applied.

## Base URL
```
http://localhost:8080/api/v1
```

## ⚠️ Important Updates
- **API Version**: All endpoints now use `/api/v1/` prefix (previously missing)
- **Field Names**: Updated to match actual entity structure
- **Response Format**: Corrected field names in response DTOs

---

## Analysis Tag Management APIs

### 1. Create Analysis Tag
**Endpoint**: `POST /api/v1/analysis-tags`

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

### 2. Get Analysis Tag by ID
**Endpoint**: `GET /api/v1/analysis-tags/{id}`

### 3. Get All Analysis Tags (Paginated)
**Endpoint**: `GET /api/v1/analysis-tags`

**Query Parameters**:
- `page` (int, default: 0): Page number (0-based)
- `size` (int, default: 20): Page size
- `sortBy` (string, default: "analysisTagId"): Sort field
- `sortDir` (string, default: "desc"): Sort direction (asc/desc)

### 4. Get Analysis Tags by Evaluation Tag ID
**Endpoint**: `GET /api/v1/analysis-tags/by-evaluation-tag/{evaluationTagId}`

### 5. Get Analysis Tags by Model
**Endpoint**: `GET /api/v1/analysis-tags/by-model?model={modelName}`

### 6. Update Analysis Tag
**Endpoint**: `PUT /api/v1/analysis-tags/{id}`

### 7. Delete Analysis Tag
**Endpoint**: `DELETE /api/v1/analysis-tags/{id}`

---

## Evaluation Analysis Management APIs

### 1. Import Analysis Results
**Endpoint**: `POST /api/v1/evaluation-analysis/import`

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

### 2. Get Analysis Result by ID
**Endpoint**: `GET /api/v1/evaluation-analysis/{id}`

**Response** (Updated field names):
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
    "standardQuestionContent": "What is the difference between..."
  }
}
```

### 3. Get All Analysis Results (Paginated)
**Endpoint**: `GET /api/v1/evaluation-analysis`

### 4. Get Analysis Results by Analysis Tag ID
**Endpoint**: `GET /api/v1/evaluation-analysis/by-tag/{analysisTagId}`

### 5. Get Overall Analysis Statistics
**Endpoint**: `GET /api/v1/evaluation-analysis/statistics`

### 6. Get Analysis Statistics by Analysis Tag ID
**Endpoint**: `GET /api/v1/evaluation-analysis/statistics/by-tag/{analysisTagId}`

### 7. Delete Analysis Result
**Endpoint**: `DELETE /api/v1/evaluation-analysis/{id}`

---

## Key Changes from Previous Documentation

### 1. API Endpoint Updates
```javascript
// OLD (INCORRECT)
const baseUrl = '/api';
const analysisTagsUrl = '/api/analysis-tags';
const evaluationAnalysisUrl = '/api/evaluation-analysis';

// NEW (CORRECT)
const baseUrl = '/api/v1';
const analysisTagsUrl = '/api/v1/analysis-tags';
const evaluationAnalysisUrl = '/api/v1/evaluation-analysis';
```

### 2. Response Field Updates
```javascript
// OLD (INCORRECT)
response.data.standardQuestionTitle

// NEW (CORRECT)
response.data.standardQuestionContent
```

### 3. Updated API Call Examples
```javascript
// Create analysis tag
const createAnalysisTag = async (data) => {
  const response = await fetch('/api/v1/analysis-tags', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return response.json();
};

// Import analysis results
const importAnalysisResults = async (data) => {
  const response = await fetch('/api/v1/evaluation-analysis/import', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return response.json();
};

// Get statistics
const getStatistics = async () => {
  const response = await fetch('/api/v1/evaluation-analysis/statistics');
  return response.json();
};

// Get analysis results with correct field access
const displayAnalysisResult = (result) => {
  console.log('Question Content:', result.standardQuestionContent); // NOT standardQuestionTitle
  console.log('Analysis Model:', result.analysisModel);
  console.log('Evaluation Model:', result.evaluationModel);
};
```

---

## Frontend Integration Updates Required

### 1. Update API Base URLs
```javascript
// Update all API calls to use /api/v1/ prefix
const API_BASE = '/api/v1';

// Update specific endpoints
const ANALYSIS_TAGS_API = `${API_BASE}/analysis-tags`;
const EVALUATION_ANALYSIS_API = `${API_BASE}/evaluation-analysis`;
```

### 2. Update Response Field Access
```javascript
// Update component code to use correct field names
const AnalysisResultComponent = ({ result }) => {
  return (
    <div>
      <h3>Question ID: {result.standardQuestionId}</h3>
      <p>Content: {result.standardQuestionContent}</p> {/* Updated field name */}
      <p>Score: {result.score}</p>
      <p>Analysis Model: {result.analysisModel}</p>
      <p>Evaluation Model: {result.evaluationModel}</p>
    </div>
  );
};
```

### 3. Update State Management
```javascript
// Update Redux/state management to use correct field names
const analysisResultReducer = (state, action) => {
  switch (action.type) {
    case 'SET_ANALYSIS_RESULT':
      return {
        ...state,
        result: {
          ...action.payload,
          questionContent: action.payload.standardQuestionContent // Map to UI-friendly name
        }
      };
  }
};
```

---

## Testing Checklist

### ✅ Backend Fixes Verified
- [x] Application starts without errors
- [x] JPQL queries execute successfully
- [x] API endpoints respond correctly
- [x] Field mappings work properly

### 🔄 Frontend Updates Required
- [ ] Update API base URLs to include `/v1/`
- [ ] Update field name references in components
- [ ] Update API service functions
- [ ] Test all CRUD operations
- [ ] Verify statistics display correctly

---

## Migration Guide for Frontend Developers

### Step 1: Update API Configuration
```javascript
// config/api.js
export const API_CONFIG = {
  BASE_URL: '/api/v1',
  ENDPOINTS: {
    ANALYSIS_TAGS: '/analysis-tags',
    EVALUATION_ANALYSIS: '/evaluation-analysis'
  }
};
```

### Step 2: Update Service Functions
```javascript
// services/analysisService.js
import { API_CONFIG } from '../config/api';

export const analysisService = {
  getAnalysisTags: (params) => 
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.ANALYSIS_TAGS}?${new URLSearchParams(params)}`),
  
  getAnalysisResults: (params) =>
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.EVALUATION_ANALYSIS}?${new URLSearchParams(params)}`)
};
```

### Step 3: Update Component Field References
```javascript
// components/AnalysisResultCard.jsx
const AnalysisResultCard = ({ result }) => (
  <Card>
    <CardHeader>
      <Title>Question {result.standardQuestionId}</Title>
    </CardHeader>
    <CardContent>
      <Text>{result.standardQuestionContent}</Text> {/* Updated field */}
      <Badge>Score: {result.score}</Badge>
    </CardContent>
  </Card>
);
```

---

## Status: ✅ Ready for Frontend Integration

The backend Phase 7 module is now fully functional and ready for frontend integration. All API endpoints are working correctly with the proper `/api/v1/` versioning and correct field names.

**Next Steps**:
1. Update frontend API calls to use correct endpoints
2. Update component field references
3. Test complete integration
4. Deploy and verify in production environment 