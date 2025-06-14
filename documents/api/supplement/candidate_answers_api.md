# Candidate Answers API Documentation

## Overview
The Candidate Answers API provides comprehensive management of candidate answers for standard questions, including import, status management, filtering, and statistics.

## Base URL
```
/api/v1/candidate-answers
```

## Endpoints

### 1. Import Candidate Answers
**POST** `/api/v1/candidate-answers/import`

Import candidate answers from CSV file.

#### Parameters
- `file` (form-data, required): CSV file containing candidate answers
- `type` (query, required): Question type (`OBJECTIVE` or `SUBJECTIVE`)

#### CSV Format

**For Objective Questions:**
```csv
std_question_id,obj_answer,notes
1,A,Correct answer for multiple choice question
2,TRUE,Correct answer for true/false question
3,C,Correct answer for another multiple choice question
```

**Fields:**
- `std_question_id` (required): ID of the standard question
- `obj_answer` (required): The CORRECT answer choice (A/B/C/D for multiple choice, TRUE/FALSE for boolean questions)
- `notes` (optional): Additional notes or comments about the answer quality/reasoning

**For Subjective Questions:**
```csv
std_question_id,sub_answer,notes
5,"Detailed answer text that explains the solution step by step",High quality answer
6,"Another comprehensive answer with examples and explanations",Good response
```

**Fields:**
- `std_question_id` (required): ID of the standard question
- `sub_answer` (required): Full text answer (enclose in quotes if contains commas)
- `notes` (optional): Additional notes or comments about the answer

**Important Notes:**
- **Objective Questions**: Each candidate answer represents the CORRECT answer choice for that question
- **Multiple Choice**: Use A, B, C, D for 4-option questions
- **True/False**: Use TRUE or FALSE for boolean questions  
- **Subjective Questions**: Can have multiple candidate answers with different perspectives
- CSV files must include header row
- Use quotes around text fields that contain commas
- Standard question must exist before importing candidate answers
- **Notes field**: Added to track answer quality, reasoning, or categorization

#### Request Example
```bash
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=OBJECTIVE" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@candidate_answers_objective.csv"
```

### 2. Get Candidate Answers (Paginated)
**GET** `/api/v1/candidate-answers`

Retrieve candidate answers with filtering and pagination.

#### Parameters
- `stdQuestionId` (query, optional): Filter by standard question ID
- `type` (query, optional): Filter by question type
- `status` (query, optional): Filter by status
- `page` (query, optional, default: 0): Page number
- `size` (query, optional, default: 10): Page size
- `sort` (query, optional, default: "id"): Sort field
- `direction` (query, optional, default: "desc"): Sort direction

### 3. Get Candidate Answer by ID
**GET** `/api/v1/candidate-answers/{id}`

### 4. Update Candidate Answer Status
**PUT** `/api/v1/candidate-answers/{id}/status`

### 5. Get Statistics
**GET** `/api/v1/candidate-answers/statistics`

### 6. Delete Candidate Answer
**DELETE** `/api/v1/candidate-answers/{id}`

### 7. Get Candidate Answers for Standard Question
**GET** `/api/v1/std-questions/{id}/candidate-answers`

---

## 📋 **CSV Format Reference**

### **File Structure Requirements**
- **Header Row**: Must be present as the first line
- **Encoding**: UTF-8 recommended
- **Delimiter**: Comma (,)
- **Text Quoting**: Use double quotes for fields containing commas or newlines

### **Objective Questions CSV**
```csv
std_question_id,obj_answer,notes
1,A,Correct answer for traffic shaping question
1,B,Alternative but incorrect option
1,C,Plausible but wrong choice
1,D,Clearly incorrect option
```

### **Subjective Questions CSV**
```csv
std_question_id,sub_answer,notes
1,"Comprehensive technical explanation with detailed steps and examples",Technical depth focused
1,"Practical solution with quick implementation steps",Implementation focused  
1,"Security-focused answer addressing potential risks and mitigations",Security focused
```

### **Design Principles**
- **Objective Questions**: Should have exactly ONE correct standard answer (best practice)
- **Subjective Questions**: Can have MULTIPLE standard answers targeting different goal points:
  - Technical depth and accuracy
  - Practical implementation focus
  - Security and best practices
  - Performance optimization
  - Beginner-friendly explanations
  - Advanced use cases

### **Quality Guidelines**
- Each candidate answer should be realistic and plausible
- Include both correct and incorrect options for objective questions
- For subjective questions, provide answers with different perspectives
- Use meaningful notes to categorize answer types and quality levels

---

## 🧪 **Test Data Examples**

### **Stack Overflow Based Examples**

**Objective Questions (candidate_answers_objective_stackoverflow.csv):**
- Questions about SSH traffic shaping, Unix stack traces, VB.NET conversion
- Each question has 4 options (A, B, C, D) with one correct answer
- Realistic technical options based on actual Stack Overflow problems

**Subjective Questions (candidate_answers_subjective_stackoverflow.csv):**
- Detailed technical explanations for complex programming problems
- Multiple answer perspectives: technical depth, practical solutions, security focus
- Real-world scenarios from Linux, development, and system administration domains

---

## 🚀 **Performance Considerations**

- Use pagination for large datasets
- Index on `std_question_id` and `status` for fast filtering
- Batch import for large CSV files
- Consider caching for frequently accessed statistics 