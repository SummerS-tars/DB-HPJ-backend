# Candidate Answers Usage Examples

## 📋 **Complete Workflow Examples**

### **Example 1: Import and Manage Objective Candidate Answers**

```bash
# 1. Import objective candidate answers from Stack Overflow problems
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=OBJECTIVE" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test-data/candidate_answers_objective_stackoverflow.csv"

# Expected Response:
{
  "success": true,
  "data": {
    "message": "候选答案导入完成",
    "importedCount": 40,
    "failedCount": 0,
    "errors": []
  }
}

# 2. Get all pending objective answers
curl "http://localhost:8080/api/v1/candidate-answers?type=OBJECTIVE&status=PENDING&size=20"

# 3. Review and accept the best answer for question 1 (SSH traffic shaping)
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/1/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED",
    "reason": "Correct technical explanation about port 443 and traffic shaping"
  }'

# 4. Reject incorrect answers
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/2/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REJECTED", 
    "reason": "Incorrect - SSH traffic is detectable regardless of port"
  }'
```

### **Example 2: Import and Manage Subjective Candidate Answers**

```bash
# 1. Import subjective candidate answers with multiple perspectives
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=SUBJECTIVE" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test-data/candidate_answers_subjective_stackoverflow.csv"

# Expected Response:
{
  "success": true,
  "data": {
    "message": "候选答案导入完成",
    "importedCount": 30,
    "failedCount": 0,
    "errors": []
  }
}

# 2. Get all subjective answers for a specific question
curl "http://localhost:8080/api/v1/std-questions/1/candidate-answers"

# 3. Accept multiple answers for different goal points
# Accept technical depth answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/41/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED",
    "reason": "Excellent technical depth with comprehensive explanation"
  }'

# Accept practical solution answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/42/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED", 
    "reason": "Great practical solution for quick implementation"
  }'

# Accept security-focused answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/43/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED",
    "reason": "Important security considerations addressed"
  }'
```

### **Example 3: Quality Review and Statistics**

```bash
# 1. Get comprehensive statistics
curl "http://localhost:8080/api/v1/candidate-answers/statistics"

# Expected Response:
{
  "success": true,
  "data": {
    "totalCount": 70,
    "countByStatus": {
      "PENDING": 45,
      "ACCEPTED": 18,
      "REJECTED": 7
    },
    "countByType": {
      "OBJECTIVE": 40,
      "SUBJECTIVE": 30
    },
    "countByTypeAndStatus": {
      "OBJECTIVE_PENDING": 25,
      "OBJECTIVE_ACCEPTED": 10,
      "OBJECTIVE_REJECTED": 5,
      "SUBJECTIVE_PENDING": 20,
      "SUBJECTIVE_ACCEPTED": 8,
      "SUBJECTIVE_REJECTED": 2
    }
  }
}

# 2. Review pending answers by type
curl "http://localhost:8080/api/v1/candidate-answers?status=PENDING&type=OBJECTIVE&sort=id&direction=asc"

# 3. Get accepted answers for quality assurance
curl "http://localhost:8080/api/v1/candidate-answers?status=ACCEPTED&size=50"
```

---

## 🎯 **Specific Use Cases**

### **Use Case 1: Stack Overflow Problem - SSH Traffic Shaping**

**Problem**: "Avoid traffic shaping by using ssh on port 443"

**Objective Candidate Answers**:
```csv
std_question_id,obj_answer,notes
1,A,Port 443 can help avoid traffic shaping by mimicking HTTPS traffic
1,B,SSH traffic is always detectable regardless of port  
1,C,Only works with specific ISP configurations
1,D,Port 443 has no effect on traffic shaping
```

**Management Workflow**:
```bash
# Import answers
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=OBJECTIVE" \
  -F "file=@ssh_traffic_answers.csv"

# Review answers for question 1
curl "http://localhost:8080/api/v1/std-questions/1/candidate-answers"

# Accept correct answer (A)
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/1/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "ACCEPTED", "reason": "Technically accurate explanation"}'

# Reject clearly wrong answer (D)  
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/4/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "REJECTED", "reason": "Factually incorrect"}'
```

### **Use Case 2: Complex Technical Question - Unix Stack Traces**

**Problem**: "Getting stack traces on Unix systems, automatically"

**Subjective Candidate Answers** (Multiple perspectives):
```csv
std_question_id,sub_answer,notes
2,"Complete implementation guide with signal handlers and backtrace()",Implementation focused
2,"Basic technical solution using backtrace() functions",Basic solution
2,"Production-ready approach with crash reporting libraries",Production focused
```

**Management Workflow**:
```bash
# Get all answers for the complex question
curl "http://localhost:8080/api/v1/std-questions/2/candidate-answers"

# Accept multiple answers for different goal points
# Implementation guide
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/5/status" \
  -d '{"status": "ACCEPTED", "reason": "Comprehensive implementation guide"}'

# Production approach  
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/7/status" \
  -d '{"status": "ACCEPTED", "reason": "Valuable production considerations"}'
```

---

## 📊 **Quality Assurance Workflows**

### **Workflow 1: Batch Review Process**

```bash
# 1. Get all pending answers for review
curl "http://localhost:8080/api/v1/candidate-answers?status=PENDING&size=100" > pending_answers.json

# 2. Review by question type
# Objective questions - should have one correct answer
curl "http://localhost:8080/api/v1/candidate-answers?type=OBJECTIVE&status=PENDING"

# Subjective questions - can have multiple good answers
curl "http://localhost:8080/api/v1/candidate-answers?type=SUBJECTIVE&status=PENDING"

# 3. Bulk status updates (example script)
# Accept high-quality technical answers
for id in 1 5 9 13; do
  curl -X PUT "http://localhost:8080/api/v1/candidate-answers/$id/status" \
    -H "Content-Type: application/json" \
    -d '{"status": "ACCEPTED", "reason": "High quality technical content"}'
done

# Reject low-quality or incorrect answers
for id in 3 7 11; do
  curl -X PUT "http://localhost:8080/api/v1/candidate-answers/$id/status" \
    -H "Content-Type: application/json" \
    -d '{"status": "REJECTED", "reason": "Insufficient technical accuracy"}'
done
```

### **Workflow 2: Question-Specific Review**

```bash
# 1. Review all answers for a specific challenging question
QUESTION_ID=6  # "How do I change the number of open files limit in Linux?"
curl "http://localhost:8080/api/v1/std-questions/$QUESTION_ID/candidate-answers"

# 2. Accept comprehensive system configuration answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/21/status" \
  -d '{
    "status": "ACCEPTED",
    "reason": "Complete system configuration with all necessary steps"
  }'

# 3. Accept troubleshooting-focused answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/22/status" \
  -d '{
    "status": "ACCEPTED", 
    "reason": "Excellent troubleshooting guidance for common issues"
  }'

# 4. Accept deployment-focused answer
curl -X PUT "http://localhost:8080/api/v1/candidate-answers/23/status" \
  -d '{
    "status": "ACCEPTED",
    "reason": "Valuable deployment and containerization considerations"
  }'
```

---

## 🔍 **Advanced Filtering Examples**

### **Filter by Multiple Criteria**

```bash
# Get accepted subjective answers sorted by ID
curl "http://localhost:8080/api/v1/candidate-answers?type=SUBJECTIVE&status=ACCEPTED&sort=id&direction=asc"

# Get all answers for questions 1-5 that are still pending
curl "http://localhost:8080/api/v1/candidate-answers?status=PENDING&page=0&size=50" | \
  jq '.data.content[] | select(.stdQuestionId <= 5)'

# Get rejected answers for quality review
curl "http://localhost:8080/api/v1/candidate-answers?status=REJECTED&size=20"
```

### **Pagination for Large Datasets**

```bash
# Get first page of results
curl "http://localhost:8080/api/v1/candidate-answers?page=0&size=10"

# Get specific page with sorting
curl "http://localhost:8080/api/v1/candidate-answers?page=2&size=25&sort=stdQuestionId&direction=asc"

# Get all pages (example script)
page=0
while true; do
  response=$(curl -s "http://localhost:8080/api/v1/candidate-answers?page=$page&size=50")
  echo "$response" | jq '.data.content[]'
  
  last=$(echo "$response" | jq '.data.last')
  if [ "$last" = "true" ]; then
    break
  fi
  ((page++))
done
```

---

## 📈 **Monitoring and Analytics**

### **Regular Statistics Monitoring**

```bash
# Daily statistics check
curl "http://localhost:8080/api/v1/candidate-answers/statistics" | \
  jq '{
    total: .data.totalCount,
    pending_ratio: (.data.countByStatus.PENDING / .data.totalCount * 100),
    acceptance_rate: (.data.countByStatus.ACCEPTED / (.data.countByStatus.ACCEPTED + .data.countByStatus.REJECTED) * 100)
  }'

# Quality metrics by type
curl "http://localhost:8080/api/v1/candidate-answers/statistics" | \
  jq '.data.countByTypeAndStatus'
```

### **Progress Tracking**

```bash
# Track review progress
echo "=== Candidate Answer Review Progress ==="
stats=$(curl -s "http://localhost:8080/api/v1/candidate-answers/statistics")

total=$(echo "$stats" | jq '.data.totalCount')
pending=$(echo "$stats" | jq '.data.countByStatus.PENDING // 0')
accepted=$(echo "$stats" | jq '.data.countByStatus.ACCEPTED // 0')
rejected=$(echo "$stats" | jq '.data.countByStatus.REJECTED // 0')

echo "Total Answers: $total"
echo "Pending Review: $pending ($(echo "scale=1; $pending * 100 / $total" | bc)%)"
echo "Accepted: $accepted ($(echo "scale=1; $accepted * 100 / $total" | bc)%)"
echo "Rejected: $rejected ($(echo "scale=1; $rejected * 100 / $total" | bc)%)"
```

---

## 🛠️ **Troubleshooting Examples**

### **Common Import Issues**

```bash
# Test import with validation
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=OBJECTIVE" \
  -F "file=@test_file.csv" 2>&1 | \
  jq '.data.errors[]? // "No errors"'

# Handle import errors
curl -X POST "http://localhost:8080/api/v1/candidate-answers/import?type=SUBJECTIVE" \
  -F "file=@problematic_file.csv" | \
  jq '.data | {imported: .importedCount, failed: .failedCount, errors: .errors}'
```

### **Data Validation**

```bash
# Verify answer counts per question
curl "http://localhost:8080/api/v1/candidate-answers?size=1000" | \
  jq '.data.content | group_by(.stdQuestionId) | map({question: .[0].stdQuestionId, count: length})'

# Check for questions without accepted answers
curl "http://localhost:8080/api/v1/candidate-answers?status=ACCEPTED&size=1000" | \
  jq '.data.content | map(.stdQuestionId) | unique'
```

This comprehensive set of examples demonstrates the full lifecycle of candidate answer management, from import through quality review to analytics and monitoring. 