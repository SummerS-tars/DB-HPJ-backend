# Candidate Answers Module - Updated Design

## 📋 **Overview**

The Candidate Answers module manages potential answers for standard questions, supporting both objective (multiple choice/true-false) and subjective (text-based) question types. Each candidate answer represents a potential correct answer that can be reviewed and accepted as a standard answer.

---

## 🗄️ **Database Schema**

### **Core Table: candidate_answers**

```sql
CREATE TABLE candidate_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    std_question_id BIGINT NOT NULL,
    type ENUM('OBJECTIVE', 'SUBJECTIVE') NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    notes TEXT COMMENT 'Additional notes about answer quality, reasoning, or categorization',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (std_question_id) REFERENCES std_questions(id),
    INDEX idx_std_question_id (std_question_id),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_notes (notes(255))
);
```

### **Answer Content Tables**

**Objective Answers:**
```sql
CREATE TABLE candidate_answers_obj (
    candidate_answer_id BIGINT PRIMARY KEY,
    obj_answer ENUM('A', 'B', 'C', 'D', 'E', 'TRUE', 'FALSE') NOT NULL,
    FOREIGN KEY (candidate_answer_id) REFERENCES candidate_answers(id) ON DELETE CASCADE
);
```

**Subjective Answers:**
```sql
CREATE TABLE candidate_answers_sub (
    candidate_answer_id BIGINT PRIMARY KEY,
    sub_answer TEXT NOT NULL,
    FOREIGN KEY (candidate_answer_id) REFERENCES candidate_answers(id) ON DELETE CASCADE
);
```

---

## 🎯 **Design Principles**

### **Objective Questions**
- **Purpose**: Store the CORRECT answer choice for the question
- **Multiple Choice**: Use A, B, C, D (or E if 5 options)
- **True/False**: Use TRUE or FALSE
- **One Answer Per Question**: Each objective question should have exactly one correct candidate answer
- **Quality Focus**: Notes field tracks reasoning for answer selection

### **Subjective Questions**
- **Purpose**: Store comprehensive text-based answers
- **Multiple Perspectives**: Can have multiple candidate answers with different approaches:
  - Technical depth and accuracy
  - Practical implementation focus
  - Security and best practices
  - Performance optimization
  - Beginner-friendly explanations
- **Quality Categorization**: Notes field categorizes answer type and quality level

---

## 📊 **Data Flow**

### **Import Process**
1. **CSV Upload**: Import candidate answers from structured CSV files
2. **Validation**: Verify standard question exists and type matches
3. **Content Creation**: Create appropriate content record (obj/sub)
4. **Status Assignment**: Set initial status to PENDING
5. **Notes Recording**: Store quality/categorization information

### **Review Process**
1. **Quality Assessment**: Review candidate answers for accuracy and completeness
2. **Status Management**: Update status (PENDING → ACCEPTED/REJECTED)
3. **Multiple Acceptance**: For subjective questions, accept multiple high-quality answers
4. **Standard Answer Creation**: Accepted candidates become standard answers

---

## 🔄 **Status Workflow**

```
PENDING (Initial state)
   ↓
   ├── ACCEPTED (High quality, becomes standard answer)
   └── REJECTED (Low quality or incorrect)
   
Status Transitions:
- PENDING → ACCEPTED: Answer meets quality standards
- PENDING → REJECTED: Answer is incorrect or low quality  
- ACCEPTED → REJECTED: Quality issues discovered later
- REJECTED → PENDING: Reconsideration of rejected answer
```

---

## 📝 **CSV Import Format**

### **Objective Questions**
```csv
std_question_id,obj_answer,notes
2,B,Implementing custom SIGSEGV handler is most effective for automatic stack traces
4,D,gettimeofday() resolution is hardware-dependent but consistent within same system
```

### **Subjective Questions**
```csv
std_question_id,sub_answer,notes
1,"Comprehensive security analysis of SSH port 443 usage with alternatives",Security-focused analysis
1,"Technical effectiveness explanation with DPI considerations",Technical depth focus
3,"Strategic migration analysis comparing Mono, .NET Core, and rewrite approaches",Strategic planning focus
```

---

## 🏗️ **Entity Relationships**

```
StandardQuestion (1) ←→ (N) CandidateAnswer
CandidateAnswer (1) ←→ (1) CandidateAnswerObj
CandidateAnswer (1) ←→ (1) CandidateAnswerSub  
CandidateAnswer (1) ←→ (N) StandardAnswer (when accepted)
```

---

## 🎨 **Quality Management**

### **Notes Field Usage**
- **Objective Questions**: 
  - "Correct technical explanation"
  - "Best practice approach"
  - "Most comprehensive solution"

- **Subjective Questions**:
  - "Security-focused analysis"
  - "Technical depth focus"
  - "Practical implementation guide"
  - "Beginner-friendly explanation"
  - "Performance optimization focus"

### **Quality Criteria**
- **Technical Accuracy**: Factually correct information
- **Completeness**: Addresses all aspects of the question
- **Clarity**: Well-structured and understandable
- **Relevance**: Directly answers the question asked
- **Best Practices**: Follows industry standards and recommendations

---

## 🔧 **API Design**

### **Key Endpoints**
- `POST /api/v1/candidate-answers/import`: CSV import with type specification
- `GET /api/v1/candidate-answers`: Filtered listing with pagination
- `PUT /api/v1/candidate-answers/{id}/status`: Status management
- `GET /api/v1/candidate-answers/statistics`: Quality metrics
- `GET /api/v1/std-questions/{id}/candidate-answers`: Question-specific answers

### **Filtering Capabilities**
- By standard question ID
- By question type (OBJECTIVE/SUBJECTIVE)
- By status (PENDING/ACCEPTED/REJECTED)
- Combined filters with pagination and sorting

---

## 📈 **Analytics and Metrics**

### **Quality Metrics**
- Total candidate answers by type
- Status distribution (pending/accepted/rejected)
- Acceptance rate by question type
- Review progress tracking

### **Performance Indicators**
- Import success rate
- Review completion percentage
- Answer quality distribution
- Time to review metrics

---

## 🚀 **Implementation Notes**

### **Database Optimizations**
- Indexes on frequently queried fields (std_question_id, status, type)
- Text index on notes field for search capabilities
- Foreign key constraints for data integrity

### **Business Logic**
- Validation ensures standard question exists before import
- Type matching between question and candidate answer
- Proper handling of notes field in all operations
- Support for both multiple choice and true/false objective questions

### **Future Enhancements**
- Batch status update operations
- Advanced search and filtering
- Answer quality scoring algorithms
- Automated duplicate detection 