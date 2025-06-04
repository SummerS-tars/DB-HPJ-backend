# API & 数据结构快速参考

## API 端点列表

### 原始问答 (Raw Q&A)

```txt
POST   /api/v1/raw-questions/import          # 导入原始问题
GET    /api/v1/raw-questions                 # 查询原始问题列表
GET    /api/v1/raw-questions/{id}            # 获取单个原始问题
PATCH  /api/v1/raw-questions/{id}/status     # 更新问题状态

POST   /api/v1/raw-answers/import            # 导入原始答案
GET    /api/v1/raw-answers                   # 查询原始答案列表
GET    /api/v1/raw-questions/{id}/answers    # 获取问题的答案
```

### 标准问题 (Standard Questions)

```txt
POST   /api/v1/versions                      # 创建版本
GET    /api/v1/versions                      # 获取版本列表

POST   /api/v1/tags                          # 创建标签
GET    /api/v1/tags                          # 获取标签列表

POST   /api/v1/std-questions/import          # 导入标准问题 *需要原始问题ID
GET    /api/v1/std-questions                 # 查询标准问题列表
GET    /api/v1/std-questions/{id}            # 获取单个标准问题
GET    /api/v1/raw-questions/{id}/std-questions # 获取由原始问题转换的标准问题

POST   /api/v1/std-questions/{id}/tags       # 添加标签
DELETE /api/v1/std-questions/{id}/tags/{tag} # 删除标签
```

### 候选答案 (Candidate Answers)

```txt
POST   /api/v1/candidate-answers/import      # 导入候选答案
GET    /api/v1/candidate-answers             # 查询候选答案列表
GET    /api/v1/std-questions/{id}/candidate-answers # 获取问题的候选答案
PATCH  /api/v1/candidate-answers/{id}        # 更新状态 *ACCEPTED时自动创建标准答案
```

### 标准答案 (Standard Answers)

```txt
GET    /api/v1/std-answers                   # 查询标准答案列表 *只能通过候选答案创建
GET    /api/v1/std-questions/{id}/std-answers # 获取问题的标准答案
PATCH  /api/v1/std-answers/{id}              # 更新标准答案
```

### 评估结果 (Evaluation Results)

```txt
POST   /api/v1/evaluation-tags               # 创建评估标签
GET    /api/v1/evaluation-tags               # 获取评估标签列表

POST   /api/v1/evaluation-results/import     # 导入评估结果
GET    /api/v1/evaluation-results            # 查询评估结果列表
GET    /api/v1/evaluation-results/export     # 导出对照数据
```

### 评估分析 (Evaluation Analysis)

```txt
POST   /api/v1/analysis-tags                 # 创建分析标签
GET    /api/v1/analysis-tags                 # 获取分析标签列表

POST   /api/v1/evaluation-analysis/import    # 导入分析结果
GET    /api/v1/evaluation-analysis           # 查询分析结果列表
```

---

## 数据结构定义

### RawQuestion

```json
{
  "id": 1,
  "title": "问题标题",
  "content": "问题内容",
  "sourcePlatform": "stackoverflow",
  "tags": "java,spring",
  "postId": 12345,
  "score": 10,
  "status": "WAITING_CONVERTED|CONVERTED|OMITTED"
}
```

### RawAnswer

```json
{
  "id": 1,
  "rawQuestionId": 1,
  "content": "答案内容",
  "sourcePlatform": "stackoverflow",
  "postId": 12346,
  "score": 5
}
```

### StandardQuestion

```json
{
  "id": 1,
  "originalRawQuestionId": 5,    // 必需：关联原始问题
  "type": "OBJECTIVE|SUBJECTIVE",
  "content": "标准化问题内容",
  "status": "WAITING_ANSWERS|ANSWERED",
  "createdAt": "2025-01-01T00:00:00Z",
  "versions": [{"version": "1.0"}],
  "tags": [{"tag": "Linux Kernel"}]
}
```

### CandidateAnswer

```json
{
  "id": 1,
  "stdQuestionId": 123,
  "type": "OBJECTIVE|SUBJECTIVE",
  "status": "PENDING|ACCEPTED|REJECTED",
  "objAnswer": "A",              // 仅OBJECTIVE类型
  "subAnswer": "详细答案内容"     // 仅SUBJECTIVE类型
}
```

### StandardAnswer

```json
{
  "id": 1,
  "stdQuestionId": 123,
  "selectedFromCandidateId": 789, // 必需：来源候选答案
  "type": "OBJECTIVE|SUBJECTIVE",
  "score": 8,                     // 0-10
  "status": "ACCEPTED|OMITTED",
  "createdAt": "2025-01-01T00:00:00Z",
  "objAnswer": "A",               // 仅OBJECTIVE类型
  "subAnswer": "标准答案内容"      // 仅SUBJECTIVE类型
}
```

### EvaluationTag

```json
{
  "tagId": 1,
  "dataSetVersion": "1.0",       // 必需：引用versions表
  "evaluationTime": 1,
  "model": "GPT-4-Turbo"
}
```

### EvaluationResult

```json
{
  "id": 1,
  "evaluationTagId": 1,
  "stdQuestionId": 123,
  "type": "OBJECTIVE|SUBJECTIVE",
  "content": "LLM生成的答案",
  "status": "PENDING|ANALYZED|OMITTED"
}
```

### AnalysisTag

```json
{
  "analysisTagId": 1,
  "evaluationTagId": 1,          // 必需：关联评估标签
  "analysisTime": 1,
  "model": "ScoringLLM-v1"
}
```

### EvaluationAnalysis

```json
{
  "id": 1,
  "evaluationResultId": 1,
  "analysisTagId": 1,
  "score": 8,                    // 0-10整数
  "createdAt": "2025-01-01T00:00:00Z"
}
```

---

## 查询参数标准

### 分页参数

- `page` (int, default: 0): 页码
- `size` (int, default: 20): 每页大小
- `sortBy` (string): 排序字段
- `sortDirection` (string, default: "asc"): asc|desc

### 筛选参数示例

- `status`: 状态筛选
- `type`: 类型筛选 (OBJECTIVE|SUBJECTIVE)
- `version`: 版本筛选
- `tags`: 标签筛选 (逗号分隔)

---

## 重要约束

1. **StandardQuestion必须关联RawQuestion**: originalRawQuestionId不能为空
2. **StandardAnswer必须来自CandidateAnswer**: selectedFromCandidateId不能为空
3. **状态流转**: CandidateAnswer设为ACCEPTED时自动创建StandardAnswer
4. **类型一致性**: 相关联的实体type字段必须一致
5. **外键约束**: 删除时注意级联关系，特别是raw_questions使用RESTRICT

---

## 响应格式

### 成功响应

```json
{
  "success": true,
  "data": {...},
  "message": "操作成功"
}
```

### 错误响应

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "请求参数无效",
    "details": ["字段xxx不能为空"]
  }
}
```

### 分页响应

使用Spring Boot标准PageImpl格式，包含content, pageable, totalElements等字段。  
