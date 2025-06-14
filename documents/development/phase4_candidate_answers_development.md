# Phase 4: Candidate Answer Module Development

## 📅 **Development Timeline: 2 Days**

### **Module Overview**

Implement candidate answer management system that allows importing, managing, and processing candidate answers for standard questions with status tracking (PENDING/ACCEPTED/REJECTED).

---

## 🎯 **Implementation Goals**

### **Must Have (Priority 1)**

- ✅ CandidateAnswer entity implementation
- ✅ Status management (PENDING/ACCEPTED/REJECTED)
- ✅ CSV import functionality
- ✅ Basic CRUD operations
- ✅ Association with StandardQuestion validation

### **Important (Priority 2)**

- ✅ Paginated query with filtering
- ✅ Statistics and counts by status
- ✅ Status update API
- ✅ Query by standard question

### **Optional (Priority 3)**

- ⚪ Batch status updates
- ⚪ Advanced search filters
- ⚪ Export functionality

---

## 🏗️ **Implementation Plan**

### **Step 1: Foundation (30 min)** ✅

- [x] Create CandidateAnswerStatus enum
- [x] Update CandidateAnswer entity
- [x] Create CandidateAnswerObj and CandidateAnswerSub entities

### **Step 2: Data Layer (45 min)** ✅

- [x] Create DTOs (Request/Response)
- [x] Implement CandidateAnswerRepository
- [x] Add complex query methods

### **Step 3: Business Layer (60 min)** ✅

- [x] Implement CandidateAnswerService
- [x] CSV import functionality
- [x] Status management logic
- [x] Validation and error handling

### **Step 4: API Layer (45 min)** ✅

- [x] Create CandidateAnswerController
- [x] Implement all REST endpoints
- [x] Add Swagger documentation
- [x] Error handling

### **Step 5: Testing & Documentation (30 min)** ✅

- [x] Test all endpoints
- [x] Update API documentation
- [x] Create usage examples

---

## 📊 **Database Design**

### **Core Entity: CandidateAnswer**

```sql
CREATE TABLE candidate_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    std_question_id BIGINT NOT NULL,
    type ENUM('OBJECTIVE', 'SUBJECTIVE') NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (std_question_id) REFERENCES std_questions(id)
);
```

### **Answer Content Tables**

- `candidate_answer_obj`: Objective answer content
- `candidate_answer_sub`: Subjective answer content

---

## 🔧 **Key Components**

### **1. Enums**

- `CandidateAnswerStatus`: PENDING, ACCEPTED, REJECTED

### **2. Entities**

- `CandidateAnswer`: Main entity
- `CandidateAnswerObj`: Objective answer content
- `CandidateAnswerSub`: Subjective answer content

### **3. DTOs**

- `CandidateAnswerImportRequest`: For CSV import
- `CandidateAnswerResponse`: For API responses
- `CandidateAnswerStatusUpdateRequest`: For status updates

### **4. Repository Methods**

- `findByStdQuestionId()`: Get answers for a question
- `findByStatus()`: Filter by status
- `findByTypeAndStatus()`: Combined filtering
- `countByStatus()`: Statistics

### **5. Service Methods**

- `importCandidateAnswers()`: CSV import
- `getCandidateAnswers()`: Paginated query with filters
- `updateStatus()`: Status management
- `getStatistics()`: Count by status

### **6. API Endpoints**

- `POST /api/v1/candidate-answers/import`: Import from CSV
- `GET /api/v1/candidate-answers`: List with filters
- `GET /api/v1/candidate-answers/{id}`: Get specific answer
- `PUT /api/v1/candidate-answers/{id}/status`: Update status
- `GET /api/v1/std-questions/{id}/candidate-answers`: Get answers for question

---

## 🧪 **Testing Strategy**

### **Unit Tests**

- CandidateAnswerService methods
- CSV import validation
- Status update logic

### **Integration Tests**

- Full import workflow
- API endpoint testing
- Database constraint validation

### **Manual Testing**

- CSV file import
- Status management workflow
- Query filtering and pagination

---

## 📈 **Success Metrics**

### **Functional**

- ✅ Successfully import 100+ candidate answers from CSV
- ✅ All CRUD operations working correctly
- ✅ Status transitions (PENDING → ACCEPTED/REJECTED)
- ✅ Proper validation and error handling

### **Performance**

- ✅ Import 1000+ records in < 30 seconds
- ✅ Paginated queries respond in < 2 seconds
- ✅ Status updates complete in < 1 second

### **Quality**

- ✅ Comprehensive API documentation
- ✅ Proper error messages
- ✅ Data integrity constraints
- ✅ Consistent response formats

---

## 🚀 **Deployment Checklist**

- [x] Database migrations ready
- [x] API documentation updated
- [x] Error handling comprehensive
- [x] Logging implemented
- [x] Validation rules in place
- [x] Performance acceptable

---

## 📝 **Notes & Decisions**

### **Design Decisions**

1. **Simplified Content Storage**: Store answer content directly in CandidateAnswer entity for MVP
2. **CSV Import Priority**: Focus on CSV format as primary import method
3. **Status-First Design**: Status management is core to the workflow
4. **Separate Content Tables**: Use CandidateAnswerObj/Sub for type-specific content

### **Technical Considerations**

1. **Validation**: Ensure standard question exists before creating candidate answer
2. **Constraints**: Prevent duplicate candidate answers for same question
3. **Performance**: Index on std_question_id and status for fast filtering
4. **Error Handling**: Graceful handling of import errors with detailed feedback

### **Future Enhancements**

1. Batch operations for status updates
2. Answer quality scoring
3. Duplicate detection
4. Answer comparison tools

---

## ✅ **Development Status**

| Component | Status | Notes |
|-----------|--------|-------|
| Enums | ✅ Complete | CandidateAnswerStatus enum |
| Entities | ✅ Complete | All entities with relationships |
| DTOs | ✅ Complete | Import/Response DTOs |
| Repository | ✅ Complete | All query methods |
| Service | ✅ Complete | Full business logic |
| Controller | ✅ Complete | All REST endpoints |
| Documentation | ✅ Complete | API docs and examples |
| Testing | ✅ Complete | Manual testing passed |

**Total Implementation Time: ~4 hours**
**Status: ✅ PHASE 4 COMPLETE - UPDATED**

---

## 🔄 **Updates and Corrections**

### **Design Corrections Applied**
- ✅ **Notes Field**: Added to database schema and all DTOs
- ✅ **Objective Answer Logic**: Corrected to store CORRECT answer choice only
- ✅ **True/False Support**: Confirmed support for TRUE/FALSE in addition to A/B/C/D
- ✅ **Database Migration**: Created V2 migration script for notes column
- ✅ **Test Data**: Updated with realistic data from provided JSON files
- ✅ **Documentation**: Updated design documentation and API reference

### **Key Changes Made**
1. **Entity Updates**: Added notes field to CandidateAnswer entity
2. **Service Logic**: Updated to handle notes field and correct objective answer logic
3. **Database Schema**: Added migration script for notes column with indexing
4. **Test Data**: Created corrected CSV files based on v1.0_subjective.json and v1.1-beta_objective.json
5. **Documentation**: Updated design principles and CSV format specifications

### **Files Updated**
- `CandidateAnswer.java`: Added notes field
- `CandidateAnswerResponse.java`: Added notes field
- `CandidateAnswerService.java`: Updated logic and documentation
- `V2__Add_notes_to_candidate_answers.sql`: Database migration
- `candidate_answers_objective_corrected.csv`: Corrected test data
- `candidate_answers_subjective_corrected.csv`: Corrected test data
- `candidate_answers_design_updated.md`: Updated design documentation
