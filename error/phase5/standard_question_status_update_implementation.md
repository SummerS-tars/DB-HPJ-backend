# Standard Question Status Update Implementation

## Overview

This document provides detailed implementation approaches for automatically updating the status of standard questions based on their related standard answers. The requirement is to maintain data consistency by updating standard question status when standard answers are created, updated, or deleted.

## Current System Analysis

### Entity Relationships
- `StandardQuestion` has `OneToMany` relationship with `StandardAnswer`
- `StandardAnswer` has `ManyToOne` relationship with `StandardQuestion`
- `CandidateAnswer` gets converted to `StandardAnswer` when accepted

### Current Status Enums
- **StandardQuestionStatus**: `WAITING_ANSWERS`, `ANSWERED`
- **StandardAnswerStatus**: `ACCEPTED`, `OMITTED`
- **CandidateAnswerStatus**: `PENDING`, `ACCEPTED`, `REJECTED`

### Current Flow
1. Candidate answers are imported with status `PENDING`
2. When a candidate answer is accepted (`CandidateAnswerStatus.ACCEPTED`), it can be converted to a standard answer
3. Standard answers are created with status `ACCEPTED` by default
4. **Missing**: Automatic update of standard question status

## Requirements Analysis

### Primary Requirements
1. **Answer Creation**: When a candidate answer is accepted and becomes a standard answer, the related standard question should be updated to `ANSWERED` status
2. **Answer Omission**: When all standard answers related to a standard question are omitted (`OMITTED` status), the standard question should be updated to `WAITING_ANSWERS` status
3. **Answer Deletion**: When standard answers are deleted, the standard question status should be updated accordingly

### Edge Cases
- Multiple standard answers for the same question
- Concurrent updates to standard answers
- Rollback scenarios when operations fail
- Performance considerations for bulk operations

## Implementation Approaches

### Approach 1: Application Layer Service Method (Recommended)

**Implementation**: Add a service method to handle standard question status updates

```java
@Service
public class StandardQuestionStatusUpdateService {
    
    @Autowired
    private StandardQuestionRepository standardQuestionRepository;
    
    @Autowired
    private StandardAnswerRepository standardAnswerRepository;
    
    /**
     * Update standard question status based on its standard answers
     */
    @Transactional
    public void updateStandardQuestionStatus(Long stdQuestionId) {
        StandardQuestion question = standardQuestionRepository.findById(stdQuestionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Standard question not found"));
        
        // Check if there are any accepted standard answers
        boolean hasAcceptedAnswers = standardAnswerRepository
            .existsByStdQuestionIdAndStatus(stdQuestionId, StandardAnswerStatus.ACCEPTED);
        
        StandardQuestionStatus newStatus = hasAcceptedAnswers ? 
            StandardQuestionStatus.ANSWERED : StandardQuestionStatus.WAITING_ANSWERS;
        
        if (question.getStatus() != newStatus) {
            question.setStatus(newStatus);
            standardQuestionRepository.save(question);
            log.info("Updated standard question {} status from {} to {}", 
                stdQuestionId, question.getStatus(), newStatus);
        }
    }
    
    /**
     * Batch update standard question statuses
     */
    @Transactional
    public void batchUpdateStandardQuestionStatus(List<Long> stdQuestionIds) {
        for (Long questionId : stdQuestionIds) {
            updateStandardQuestionStatus(questionId);
        }
    }
}
```

**Integration Points**:
1. `StandardAnswerService.createFromCandidateAnswer()` - call after creating standard answer
2. `StandardAnswerService.updateStandardAnswer()` - call when status changes
3. `StandardAnswerService.deleteStandardAnswer()` - call after deletion

**Pros**:
- Full control over business logic
- Easy to add logging and error handling
- Can handle complex business rules
- Testable and maintainable

**Cons**:
- Requires manual integration at each touch point
- Risk of forgetting to call the update method
- Additional service layer code

### Approach 2: JPA Entity Listeners (Event-Driven)

**Implementation**: Use JPA entity listeners to automatically trigger status updates

```java
@Entity
@EntityListeners(StandardAnswerListener.class)
public class StandardAnswer {
    // existing fields
}

@Component
public class StandardAnswerListener {
    
    @Autowired
    private StandardQuestionStatusUpdateService statusUpdateService;
    
    @PostPersist
    public void onStandardAnswerCreated(StandardAnswer standardAnswer) {
        statusUpdateService.updateStandardQuestionStatus(standardAnswer.getStdQuestionId());
    }
    
    @PostUpdate
    public void onStandardAnswerUpdated(StandardAnswer standardAnswer) {
        statusUpdateService.updateStandardQuestionStatus(standardAnswer.getStdQuestionId());
    }
    
    @PostRemove
    public void onStandardAnswerDeleted(StandardAnswer standardAnswer) {
        statusUpdateService.updateStandardQuestionStatus(standardAnswer.getStdQuestionId());
    }
}
```

**Pros**:
- Automatic triggering - no manual integration needed
- Consistent behavior across all operations
- Follows JPA lifecycle events

**Cons**:
- Less control over when updates happen
- Harder to debug
- Potential for circular dependencies
- Performance overhead for every entity operation

### Approach 3: Database Triggers (Database Level)

**Implementation**: Create database triggers to automatically update status

```sql
-- Trigger for INSERT on std_answers
CREATE TRIGGER trg_std_answer_insert
AFTER INSERT ON std_answers
FOR EACH ROW
BEGIN
    UPDATE std_questions 
    SET status = 'ANSWERED' 
    WHERE id = NEW.std_question_id 
    AND EXISTS (
        SELECT 1 FROM std_answers 
        WHERE std_question_id = NEW.std_question_id 
        AND status = 'ACCEPTED'
    );
END;

-- Trigger for UPDATE on std_answers
CREATE TRIGGER trg_std_answer_update
AFTER UPDATE ON std_answers
FOR EACH ROW
BEGIN
    DECLARE accepted_count INT;
    
    SELECT COUNT(*) INTO accepted_count
    FROM std_answers 
    WHERE std_question_id = NEW.std_question_id 
    AND status = 'ACCEPTED';
    
    IF accepted_count > 0 THEN
        UPDATE std_questions SET status = 'ANSWERED' WHERE id = NEW.std_question_id;
    ELSE
        UPDATE std_questions SET status = 'WAITING_ANSWERS' WHERE id = NEW.std_question_id;
    END IF;
END;

-- Trigger for DELETE on std_answers
CREATE TRIGGER trg_std_answer_delete
AFTER DELETE ON std_answers
FOR EACH ROW
BEGIN
    DECLARE accepted_count INT;
    
    SELECT COUNT(*) INTO accepted_count
    FROM std_answers 
    WHERE std_question_id = OLD.std_question_id 
    AND status = 'ACCEPTED';
    
    IF accepted_count > 0 THEN
        UPDATE std_questions SET status = 'ANSWERED' WHERE id = OLD.std_question_id;
    ELSE
        UPDATE std_questions SET status = 'WAITING_ANSWERS' WHERE id = OLD.std_question_id;
    END IF;
END;
```

**Pros**:
- Guarantees consistency at database level
- No application code needed
- Handles all operations automatically
- High performance

**Cons**:
- Database-specific implementation
- Harder to test and debug
- Limited business logic capabilities
- Difficult to maintain and version control

### Approach 4: Spring Events (Decoupled Architecture)

**Implementation**: Use Spring's event publishing mechanism

```java
// Event classes
public class StandardAnswerCreatedEvent {
    private final Long stdQuestionId;
    private final Long standardAnswerId;
    // constructors, getters
}

public class StandardAnswerStatusChangedEvent {
    private final Long stdQuestionId;
    private final StandardAnswerStatus oldStatus;
    private final StandardAnswerStatus newStatus;
    // constructors, getters
}

// Event publisher in service
@Service
public class StandardAnswerService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public StandardAnswerResponse createFromCandidateAnswer(StandardAnswerCreateRequest request) {
        // existing logic
        StandardAnswer saved = standardAnswerRepository.save(standardAnswer);
        
        // Publish event
        eventPublisher.publishEvent(new StandardAnswerCreatedEvent(
            saved.getStdQuestionId(), saved.getId()));
        
        return convertToResponse(saved);
    }
}

// Event listener
@Component
public class StandardQuestionStatusEventListener {
    
    @Autowired
    private StandardQuestionStatusUpdateService statusUpdateService;
    
    @EventListener
    @Async
    public void handleStandardAnswerCreated(StandardAnswerCreatedEvent event) {
        statusUpdateService.updateStandardQuestionStatus(event.getStdQuestionId());
    }
    
    @EventListener
    @Async
    public void handleStandardAnswerStatusChanged(StandardAnswerStatusChangedEvent event) {
        statusUpdateService.updateStandardQuestionStatus(event.getStdQuestionId());
    }
}
```

**Pros**:
- Decoupled architecture
- Can be asynchronous
- Easy to add additional listeners
- Follows Spring best practices

**Cons**:
- More complex setup
- Potential for lost events if not handled properly
- Harder to debug event flow

### Approach 5: Hybrid Approach (Application + Database)

**Implementation**: Combine application layer logic with database constraints

```java
// Application layer service (same as Approach 1)
@Service
public class StandardQuestionStatusUpdateService {
    // implementation from Approach 1
}

// Database constraint to ensure consistency
CREATE TRIGGER trg_consistency_check
BEFORE UPDATE ON std_questions
FOR EACH ROW
BEGIN
    IF NEW.status = 'ANSWERED' THEN
        IF NOT EXISTS (
            SELECT 1 FROM std_answers 
            WHERE std_question_id = NEW.id 
            AND status = 'ACCEPTED'
        ) THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Cannot set status to ANSWERED without accepted answers';
        END IF;
    END IF;
END;
```

**Pros**:
- Double protection against inconsistency
- Flexibility of application logic
- Database-level safety net

**Cons**:
- Most complex to implement
- Higher maintenance overhead
- Potential for conflicts between layers

## Recommended Implementation

### Phase 1: Core Service Implementation (Approach 1)

1. **Create StatusUpdateService**: Implement the core logic for updating standard question status
2. **Add Repository Methods**: Create necessary repository methods for status checking
3. **Integrate with Existing Services**: Update StandardAnswerService methods to call status update
4. **Add Comprehensive Logging**: Track all status changes for audit purposes

### Phase 2: Event-Driven Enhancement (Approach 4)

1. **Add Event Classes**: Define events for standard answer lifecycle
2. **Implement Event Publishing**: Integrate with existing service methods
3. **Create Event Listeners**: Handle status updates asynchronously
4. **Add Event Monitoring**: Track event processing for debugging

### Phase 3: Batch Processing Support

1. **Bulk Update Methods**: Handle multiple question status updates efficiently
2. **Scheduled Jobs**: Periodic consistency checks and corrections
3. **Performance Optimization**: Optimize queries for large datasets

## Implementation Plan

### Step 1: Add Required Repository Methods

```java
// In StandardAnswerRepository
boolean existsByStdQuestionIdAndStatus(Long stdQuestionId, StandardAnswerStatus status);
long countByStdQuestionIdAndStatus(Long stdQuestionId, StandardAnswerStatus status);
```

### Step 2: Create Status Update Service

```java
@Service
@Transactional
public class StandardQuestionStatusUpdateService {
    // Implementation from Approach 1
}
```

### Step 3: Integrate with Existing Services

Update these methods in `StandardAnswerService`:
- `createFromCandidateAnswer()` - add status update call
- `updateStandardAnswer()` - add status update call when status changes
- `deleteStandardAnswer()` - add status update call

### Step 4: Add Comprehensive Testing

- Unit tests for status update logic
- Integration tests for service interactions
- Edge case testing (concurrent updates, rollbacks)
- Performance testing for bulk operations

### Step 5: Documentation and Monitoring

- Update API documentation
- Add monitoring metrics for status updates
- Create troubleshooting guides

## Error Handling and Recovery

### Transactional Integrity
- Wrap status updates in transactions
- Implement rollback mechanisms
- Handle constraint violations gracefully

### Consistency Checks
- Periodic background jobs to verify consistency
- Automated correction of inconsistent states
- Alert system for critical inconsistencies

### Performance Considerations
- Batch processing for bulk updates
- Indexing on frequently queried columns
- Caching for frequently accessed data

## Testing Strategy

### Unit Tests
- Test status update logic with various scenarios
- Mock repository interactions
- Verify business rule enforcement

### Integration Tests
- Test complete workflow from candidate answer to standard question status
- Verify transactional behavior
- Test concurrent access scenarios

### Performance Tests
- Measure impact of status updates on system performance
- Test with large datasets
- Optimize based on results

## Conclusion

The recommended approach is to start with **Approach 1 (Application Layer Service Method)** for its simplicity and control, then enhance with **Approach 4 (Spring Events)** for better decoupling. This provides a solid foundation that can be extended with additional features as needed.

The implementation should prioritize:
1. **Data Consistency**: Ensure standard question status always reflects the reality of its answers
2. **Performance**: Minimize impact on existing operations
3. **Maintainability**: Keep the code clean and well-documented
4. **Testability**: Comprehensive test coverage for all scenarios
5. **Monitoring**: Proper logging and metrics for troubleshooting

This approach provides a robust solution that can handle the current requirements while being extensible for future enhancements. 