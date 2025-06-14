# Evaluation Tag Filter and Raw Answers Import Fix Documentation

## Overview

This document records the fixes for two critical issues in the LLM evaluation system:

1. **Evaluation Tag Filter Issue**: The `evaluationTime` parameter filtering was not working
2. **Raw Answers Import Issue**: The CSV import was failing due to incorrect ID mapping

## Issue 1: Evaluation Tag Filter Problem

### Problem Description

**Request**: `GET /api/v1/evaluation-tags?page=0&size=20&dataSetVersion=v1.0&evaluationTime=2`

**Expected**: Filter evaluation tags by `evaluationTime=2`

**Actual**: The `evaluationTime` parameter was ignored, returning all tags with `dataSetVersion=v1.0`

**SQL Evidence**: The generated SQL only included `WHERE et1_0.data_set_version=?` without the `evaluation_time` condition.

### Root Cause Analysis

The issue existed at three layers:

1. **Controller Layer**: Missing `evaluationTime` parameter in `getEvaluationTags()` method
2. **Service Layer**: Method signature didn't accept `evaluationTime` parameter
3. **Repository Layer**: Missing query methods for `evaluationTime` filtering

### Solution Implementation

#### 1. Controller Layer Fix

**File**: `EvaluationTagController.java`

```java
// BEFORE
@GetMapping
public ResponseEntity<...> getEvaluationTags(
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "20") int size,
    @RequestParam(value = "sortBy", defaultValue = "tagId") String sortBy,
    @RequestParam(value = "order", defaultValue = "desc") String order,
    @RequestParam(value = "model", required = false) String model,
    @RequestParam(value = "dataSetVersion", required = false) String dataSetVersion) {

// AFTER
@GetMapping
public ResponseEntity<...> getEvaluationTags(
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "20") int size,
    @RequestParam(value = "sortBy", defaultValue = "tagId") String sortBy,
    @RequestParam(value = "order", defaultValue = "desc") String order,
    @RequestParam(value = "model", required = false) String model,
    @RequestParam(value = "dataSetVersion", required = false) String dataSetVersion,
    @Parameter(description = "评估次数筛选")
    @RequestParam(value = "evaluationTime", required = false) Integer evaluationTime) {
```

#### 2. Service Layer Fix

**File**: `EvaluationTagService.java`

```java
// BEFORE
public Page<EvaluationTagResponse> getEvaluationTags(int page, int size, String sortBy, String order,
                                                     String model, String dataSetVersion) {

// AFTER
public Page<EvaluationTagResponse> getEvaluationTags(int page, int size, String sortBy, String order,
                                                     String model, String dataSetVersion, Integer evaluationTime) {
```

**Enhanced Filtering Logic**:
```java
// Handle all combinations of filters
if (model != null && dataSetVersion != null && evaluationTime != null) {
    tagPage = evaluationTagRepository.findByModelAndDataSetVersionAndEvaluationTime(model, dataSetVersion, evaluationTime, pageable);
} else if (model != null && dataSetVersion != null) {
    tagPage = evaluationTagRepository.findByModelAndDataSetVersion(model, dataSetVersion, pageable);
} else if (model != null && evaluationTime != null) {
    tagPage = evaluationTagRepository.findByModelAndEvaluationTime(model, evaluationTime, pageable);
} else if (dataSetVersion != null && evaluationTime != null) {
    tagPage = evaluationTagRepository.findByDataSetVersionAndEvaluationTime(dataSetVersion, evaluationTime, pageable);
} else if (evaluationTime != null) {
    tagPage = evaluationTagRepository.findByEvaluationTime(evaluationTime, pageable);
} else {
    // ... existing logic
}
```

#### 3. Repository Layer Fix

**File**: `EvaluationTagRepository.java`

Added missing query methods:

```java
/**
 * Find evaluation tags by evaluation time with pagination
 */
Page<EvaluationTag> findByEvaluationTime(Integer evaluationTime, Pageable pageable);

/**
 * Find evaluation tags by model and evaluation time with pagination
 */
Page<EvaluationTag> findByModelAndEvaluationTime(String model, Integer evaluationTime, Pageable pageable);

/**
 * Find evaluation tags by data set version and evaluation time with pagination
 */
Page<EvaluationTag> findByDataSetVersionAndEvaluationTime(String dataSetVersion, Integer evaluationTime, Pageable pageable);

/**
 * Find evaluation tags by model, data set version and evaluation time with pagination
 */
Page<EvaluationTag> findByModelAndDataSetVersionAndEvaluationTime(String model, String dataSetVersion, Integer evaluationTime, Pageable pageable);
```

## Issue 2: Raw Answers Import Problem

### Problem Description

**Symptom**: Only answers with `rawQuestionId=88` were imported successfully, all others failed

**Root Cause**: The CSV file's `rawQuestionId` field contains the `postId` of raw questions (from source platform), but the import logic expected database `id` values.

### Data Flow Analysis

```
CSV File: rawQuestionId,content,postId,score
          123,answer content,456,5

Current Logic (WRONG):
1. Parse rawQuestionId = 123
2. Check: rawQuestionRepository.existsById(123) // Looks for database ID
3. FAIL: No raw question with database ID = 123

Correct Logic (FIXED):
1. Parse rawQuestionId = 123 (actually postId)
2. Find: rawQuestionRepository.findByPostId(123) // Looks for postId
3. SUCCESS: Found raw question with postId = 123
4. Use: rawQuestion.getId() as the actual database ID
```

### Solution Implementation

**File**: `RawAnswerService.java`

#### Import Logic Fix

```java
// BEFORE
// Validate that raw question exists
if (!rawQuestionRepository.existsById(request.getRawQuestionId())) {
    log.warn("Raw question not found: id={}", request.getRawQuestionId());
    errors.add(ImportResponse.ImportError.builder()
            .originalRecord(line)
            .error("原始问题不存在，ID: " + request.getRawQuestionId())
            .build());
    failedCount++;
    continue;
}

RawAnswer answer = modelMapper.map(request, RawAnswer.class);
rawAnswerRepository.save(answer);

// AFTER
// Find raw question by postId (not database ID)
Optional<RawQuestion> rawQuestionOpt = rawQuestionRepository.findByPostId(request.getRawQuestionId().intValue());
if (rawQuestionOpt.isEmpty()) {
    log.warn("Raw question not found by postId: {}", request.getRawQuestionId());
    errors.add(ImportResponse.ImportError.builder()
            .originalRecord(line)
            .error("原始问题不存在，PostID: " + request.getRawQuestionId())
            .build());
    failedCount++;
    continue;
}

RawQuestion rawQuestion = rawQuestionOpt.get();

RawAnswer answer = modelMapper.map(request, RawAnswer.class);
// Set the correct database ID of the raw question
answer.setRawQuestionId(rawQuestion.getId());
rawAnswerRepository.save(answer);
```

#### Documentation Update

```java
/**
 * Parse CSV line to RawAnswerImportRequest
 * Expected format: rawQuestionPostId,content,postId,score
 * Note: The first field is the postId of the raw question (not database ID)
 */
```

#### Required Imports Added

```java
import top.thesumst.llm_eval_backend.entity.RawQuestion;
import java.util.Optional;
```

## Testing and Verification

### Evaluation Tag Filter Testing

**Test Request**:
```
GET /api/v1/evaluation-tags?page=0&size=20&dataSetVersion=v1.0&evaluationTime=2
```

**Expected SQL**:
```sql
SELECT et1_0.tag_id, et1_0.created_at, et1_0.data_set_version, et1_0.evaluation_time, et1_0.model 
FROM evaluation_tags et1_0 
WHERE et1_0.data_set_version=? AND et1_0.evaluation_time=? 
ORDER BY et1_0.tag_id DESC 
LIMIT ?
```

### Raw Answers Import Testing

**Test CSV Format**:
```csv
rawQuestionId,content,postId,score
88,This is an answer,12345,5
123,Another answer,12346,3
```

**Expected Behavior**:
1. Find raw question with `postId=88` → Success
2. Find raw question with `postId=123` → Success (if exists)
3. Import both answers with correct `rawQuestionId` (database IDs)

## Impact Assessment

### Evaluation Tag Filter Fix
- **Scope**: All evaluation tag filtering operations
- **Benefit**: Proper multi-dimensional filtering capability
- **Risk**: Low - additive functionality, backward compatible

### Raw Answers Import Fix
- **Scope**: All raw answer CSV imports
- **Benefit**: Correct import success rate (from ~1% to expected ~95%+)
- **Risk**: Low - fixes existing broken functionality

## Future Improvements

### Evaluation Tags
1. Add validation for `evaluationTime` parameter range
2. Consider adding combined filter endpoints for complex queries
3. Add caching for frequently accessed filter combinations

### Raw Answers Import
1. Add CSV format validation with clear error messages
2. Consider supporting both postId and database ID formats
3. Add import preview functionality
4. Implement batch processing for large files

## Technical Debt Resolution

Both fixes address fundamental architectural issues:

1. **Missing Parameter Propagation**: Fixed the complete chain from Controller → Service → Repository
2. **Incorrect Data Mapping**: Fixed the semantic mismatch between CSV data and database expectations
3. **Incomplete API Documentation**: Updated Swagger documentation and code comments

## Conclusion

These fixes resolve critical functionality gaps in the evaluation system:

- **Evaluation Tag Filtering**: Now supports all parameter combinations including `evaluationTime`
- **Raw Answers Import**: Now correctly maps CSV data to database entities

Both fixes maintain backward compatibility while significantly improving system functionality and user experience. 