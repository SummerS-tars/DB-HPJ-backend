# Phase 5: Standard Answers Module Development Process

## Overview
Implementation of the Standard Answers module for the LLM evaluation dataset management system. This module handles the creation and management of standard answers derived from candidate answers.

## Development Timeline
**Estimated Duration**: 1-2 days  
**Start Date**: 2024-06-14  
**Current Status**: 🚧 In Progress

## Implementation Plan

### 5.1. Entity Layer Implementation
- [ ] Create `StandardAnswer` entity with proper relationships
- [ ] Create sub-entities for objective/subjective answers
- [ ] Define entity relationships with `StandardQuestion` and `CandidateAnswer`
- [ ] Add validation annotations

### 5.2. Repository Layer Implementation  
- [ ] Create `StandardAnswerRepository` interface
- [ ] Implement custom query methods for filtering and statistics
- [ ] Add methods for finding questions without standard answers

### 5.3. DTO Layer Implementation
- [ ] Create request DTOs for standard answer operations
- [ ] Create response DTOs for API responses
- [ ] Implement proper mapping between entities and DTOs

### 5.4. Service Layer Implementation
- [ ] Create `StandardAnswerService` with core business logic
- [ ] Implement automatic creation from candidate answers
- [ ] Add score management and status updates
- [ ] Implement query methods for unanswered questions

### 5.5. Controller Layer Implementation
- [ ] Create `StandardAnswerController` with REST endpoints
- [ ] Add comprehensive API documentation
- [ ] Implement proper error handling

### 5.6. Testing and Validation
- [ ] Test standard answer creation from candidate answers
- [ ] Validate score management functionality
- [ ] Test query operations and filtering
- [ ] Verify API endpoints with sample data

## Technical Requirements

### Entity Design
```java
@Entity
@Table(name = "std_answers")
public class StandardAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "std_question_id", nullable = false)
    private Long stdQuestionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandardAnswerStatus status;
    
    @Column(name = "source_candidate_answer_id")
    private Long sourceCandidateAnswerId;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal score;
    
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Relationships and sub-entities
}
```

### Key Features to Implement
1. **Automatic Creation**: Create standard answers from accepted candidate answers
2. **Score Management**: Handle scoring and validation
3. **Status Tracking**: Manage answer status lifecycle
4. **Query Operations**: Find questions without standard answers
5. **Statistics**: Provide answer coverage and score statistics

## API Endpoints Design

### Core Endpoints
- `POST /api/v1/std-answers/from-candidate/{candidateId}` - Create from candidate answer
- `GET /api/v1/std-answers` - List with filtering and pagination
- `GET /api/v1/std-answers/{id}` - Get specific standard answer
- `PUT /api/v1/std-answers/{id}` - Update standard answer
- `DELETE /api/v1/std-answers/{id}` - Delete standard answer
- `GET /api/v1/std-answers/statistics` - Get statistics
- `GET /api/v1/std-questions/without-answers` - Find questions without standard answers

## Implementation Progress

### ✅ Completed Tasks
- [x] CSV parsing fix for candidate answers (commas in quoted fields)
- [x] PATCH endpoint fix for candidate answer status updates
- [x] Analysis of existing StandardAnswer entity structure
- [x] **Entity Layer**: StandardAnswer, StandardAnswerObj, StandardAnswerSub entities already exist
- [x] **Repository Layer**: StandardAnswerRepository interface created with comprehensive query methods
- [x] **DTO Layer**: Created StandardAnswerCreateRequest, StandardAnswerUpdateRequest, StandardAnswerResponse, StandardAnswerStatisticsResponse
- [x] **Service Layer**: StandardAnswerService already exists with full business logic
- [x] **Controller Layer**: StandardAnswerController already exists with comprehensive REST endpoints

### 🚧 Current Task
- [ ] **Testing**: Test standard answer creation from candidate answers
- [ ] **Validation**: Verify all API endpoints work correctly

### 📋 Next Steps
1. Analyze existing entity structure and relationships
2. Create StandardAnswer entity and sub-entities
3. Implement repository layer
4. Build service layer with core business logic
5. Create controller with REST endpoints
6. Test and validate functionality

## Notes and Decisions
- Standard answers will be created from accepted candidate answers
- Support both objective and subjective answer types
- Maintain traceability to source candidate answers
- Implement proper validation and error handling
- Follow existing code patterns and conventions

## Issues and Resolutions
- **Issue**: CSV parsing with commas in subjective answers
- **Resolution**: ✅ Fixed with proper regex-based CSV parsing and quote handling

---
*Last Updated: 2024-06-14* 