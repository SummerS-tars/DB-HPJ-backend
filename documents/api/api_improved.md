# LLM评估数据集管理系统 - API文档 (改进版)

**基础URL**: `/api/v1`
**数据格式**: JSON (application/json)
**约定**:

- 请求参数使用camelCase，数据库字段映射在后端处理
- 分页响应统一使用Spring Boot PageImpl格式
- 错误响应包含统一的错误码和消息

---

## 1. 原始问答模块 (Raw Q&A)

### 1.1. 原始问题 (Raw Questions)

#### `POST /raw-questions/import`

- **描述**: 批量导入原始问题
- **请求**: `multipart/form-data`
- **参数**: `sourcePlatform` (query, optional): 数据来源平台  
    目前只需要考虑一种`stackoverflow`  
- **响应**:

```json
{
  "message": "导入完成",
  "importedCount": 100,
  "failedCount": 5,
  "errors": [{"row": 6, "error": "缺少必需字段title"}]
}
```

- **源数据格式**：  
    XML格式  
    行属性有：
    - Id 发布平台的ID
    - PostTypeId 发布类型
    - AcceptedAnswerId 接受回答的ID
    - CreationDate 发布时间
    - Score 评分
    - ViewCount 浏览量
    - Body 内容
    - OwnerUserId 发布者ID
    - OwnerDisplayName 发布者显示名
    - LastEditorUserId 最后编辑者ID
    - LastEditDate 最后编辑时间
    - LastActivityDate 最后活动时间
    - Title 标题
    - Tags 标签
    - AnswerCount 回答数
    - CommentCount 评论数
    - ContentLicense 内容许可证

    示例：  

    ```xml
      <row Id="27266" PostTypeId="1" AcceptedAnswerId="27280" CreationDate="2008-08-26T02:11:52.750" Score="6" ViewCount="6361" Body="&lt;p&gt;I heard that if you use port 443 (the port usually used for https) for ssh, the encrypted packets look the same to your isp.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Could this be a way to avoid traffic shaping/throttling?&lt;/p&gt;&#xA;" OwnerUserId="2908" OwnerDisplayName="Jim Robert" LastEditorUserId="508666" LastEditDate="2015-02-21T21:47:00.567" LastActivityDate="2015-02-21T21:47:00.567" Title="Avoid traffic shaping by using ssh on port 443"    ="|linux|ssh|https|trafficshaping|" AnswerCount="3" CommentCount="0" ContentLicense="CC BY-SA 2.5" />
    ```

    要求细节：  
    1. 只取需要的属性，其他的属性可以忽略  
    2. Tags的格式去掉首尾的`|`，其他的换成`,`  
    3. 便于后续标签处理  
    4. 标题和内容需要去除html标签  
    5. 如果不太好做考虑先用外部工具处理，进行数据清洗  

#### `GET /raw-questions`

- **描述**: 查询原始问题列表，支持分页、排序、筛选
- **参数**:
    - `page` (int, default: 0): 页码
    - `size` (int, default: 20): 每页大小
    - `sortBy` (string, default: "id"): 排序字段 [id, score, title]
    - `sortDirection` (string, default: "asc"): 排序方向 [asc, desc]
    - `status` (string, optional): 状态筛选 [WAITING_CONVERTED, CONVERTED, OMITTED]
    - `sourcePlatform` (string, optional): 平台筛选
- **响应**: Spring Boot分页对象

#### `GET /raw-questions/{id}`

- **描述**: 获取单个原始问题详情
- **响应**:

```json
{
  "id": 1,
  "title": "问题标题",
  "content": "问题内容",
  "sourcePlatform": "stackoverflow",
  "tags": "java,spring",
  "postId": 12345,
  "score": 10,
  "status": "WAITING_CONVERTED",
  "stdQuestionCount": 0  // 已转换的标准问题数量
}
```

#### `PATCH /raw-questions/{id}/status`

- **描述**: 更新原始问题状态
- **请求体**: `{"status": "CONVERTED"}`
- **用途**: 当原始问题被转换为标准问题时调用

### 1.2. 原始答案 (Raw Answers)

#### `POST /raw-answers/import`

- **描述**: 批量导入原始答案
- **请求**: `multipart/form-data`
- **参数**: `sourcePlatform` (query, optional)
    同样暂时只考虑`stackoverflow`  
- **源数据格式**：  
    XML格式  
    行属性有：
    - Id 发布平台的ID
    - PostTypeId 发布类型
    - ParentId 父ID，很重要，导入时根据这个找到父问题，然后关联起来
    - CreationDate 发布时间
    - Score 评分
    - Body 内容
    - OwnerUserId 发布者ID
    - OwnerDisplayName 发布者显示名
    - LastActivityDate 最后活动时间
    - CommentCount 评论数
    - ContentLicense 内容许可证

    示例：  

    ```xml
      <row Id="46075" PostTypeId="2" ParentId="32027" CreationDate="2008-09-05T15:43:46.117" Score="1" Body="&lt;p&gt;It is worth noting that a lot of tools like Nant run on mono 'out of the box', i.e.&lt;/p&gt;&#xA;&#xA;&lt;pre&gt;&lt;code&gt;mono nant.exe&#xA;&lt;/code&gt;&lt;/pre&gt;&#xA;&#xA;&lt;p&gt;works&lt;/p&gt;&#xA;" OwnerUserId="3024" OwnerDisplayName="Frep D-Oronge" LastActivityDate="2008-09-05T15:43:46.117" CommentCount="0" ContentLicense="CC BY-SA 2.5" />
    ```

    细节要求：  
    1. 只取需要的属性，其他的属性可以忽略  
    2. 内容需要去除html标签  
    3. 注意ParentId和Id的关联，导入时根据这个找到父问题，然后关联起来  
    4. 如果不太好做考虑先用外部工具处理，进行数据清洗  

#### `GET /raw-answers`

- **描述**: 查询原始答案列表
- **参数**: 标准分页排序参数

#### `GET /raw-questions/{questionId}/answers`

- **描述**: 获取指定原始问题的答案
- **参数**: 标准分页排序参数

---

## 2. 标准问题模块 (Standard Questions)

### 2.1. 版本管理 (Versions)

#### `POST /versions`

- **请求体**: `{"version": "1.2-alpha"}`
- **响应**: `{"version": "1.2-alpha", "createdAt": "2025-01-01T00:00:00Z"}`

#### `GET /versions`

- **响应**: `[{"version": "1.0"}, {"version": "1.1"}]`

### 2.2. 标签管理 (Tags)

#### `POST /tags`

- **请求体**: `{"tag": "Linux Kernel"}`

#### `GET /tags`

- **响应**: `[{"tag": "System Calls"}, {"tag": "Memory Management"}]`

### 2.3. 标准问题 (Standard Questions)

#### `POST /std-questions/import`

- **描述**: 批量导入标准问题 (必须关联原始问题)
- **请求体**:

```json
[
  {
    "originalRawQuestionId": 1,  // 必需：关联的原始问题ID
    "type": "SUBJECTIVE",
    "content": "标准化后的问题内容...",
    "status": "WAITING_ANSWERS",
    "versionIds": ["1.0", "1.1"],
    "tagNames": ["Linux Kernel", "System Calls"]
  }
]
```

#### `GET /std-questions`

- **描述**: 查询标准问题列表
- **参数**:
    - 标准分页排序参数
    - `type` (enum, required): [OBJECTIVE, SUBJECTIVE]
    - `status` (enum, optional): [WAITING_ANSWERS, ANSWERED]
    - `version` (string, optional): 版本筛选
    - `tags` (string, optional): 标签筛选 (逗号分隔)
    - `originalRawQuestionId` (int, optional): 原始问题ID筛选
- **响应**:

```json
{
  "content": [
    {
      "id": 1,
      "originalRawQuestionId": 5,
      "type": "SUBJECTIVE",
      "content": "...",
      "status": "ANSWERED",
      "createdAt": "2025-06-01T10:00:00Z",
      "versions": [{"version": "1.0"}],
      "tags": [{"tag": "Linux Kernel"}],
      "originalRawQuestion": {  // 关联的原始问题信息
        "id": 5,
        "title": "原始问题标题",
        "sourcePlatform": "stackoverflow"
      }
    }
  ]
}
```

#### `GET /std-questions/{id}`

- **描述**: 获取单个标准问题详情
- **响应**: 包含完整的关联信息

#### `GET /raw-questions/{rawQuestionId}/std-questions`

- **描述**: 获取从某个原始问题转换的所有标准问题
- **参数**: 标准分页排序参数

#### `POST /std-questions/{id}/tags`

- **请求体**: `{"tagName": "new_tag"}`

#### `DELETE /std-questions/{id}/tags/{tagName}`

---

## 3. 候选答案模块 (Candidate Answers)

### 3.1. 候选答案 (Candidate Answers)

#### `POST /candidate-answers/import`

- **参数**: `type` (enum, required): [OBJECTIVE, SUBJECTIVE]
- **请求**: CSV文件包含std_question_id和答案内容
    在导入时需要选择对应的type（OBJECTIVE或SUBJECTIVE）  

#### `GET /candidate-answers`

- **参数**:
    - `type` (enum, required): [OBJECTIVE, SUBJECTIVE]
    - `status` (enum, optional): [PENDING, ACCEPTED, REJECTED]
    - 标准分页排序参数

#### `GET /std-questions/{questionId}/candidate-answers`

- **描述**: 获取指定标准问题的候选答案

#### `PATCH /candidate-answers/{id}`

- **描述**: 更新候选答案状态，如果设为ACCEPTED则自动创建标准答案
- **请求体**: `{"status": "ACCEPTED", "score": 8}`  // score在接受时必需
- **后端逻辑**: 状态为ACCEPTED时自动创建std_answers记录

---

## 4. 标准答案模块 (Standard Answers)

**重要**: 标准答案只能通过接受候选答案创建，不支持直接创建

### 4.1. 标准答案 (Standard Answers)

#### `GET /std-answers`

- **参数**:
    - `type` (enum, required): [OBJECTIVE, SUBJECTIVE]
    - `status` (enum, optional): [ACCEPTED, OMITTED]
    - 标准分页排序参数

#### `GET /std-questions/{questionId}/std-answers`

- **响应**:

```json
{
  "content": [
    {
      "id": 1,
      "stdQuestionId": 123,
      "selectedFromCandidateId": 789,  // 必需：来源候选答案ID
      "type": "OBJECTIVE",
      "score": 10,
      "status": "ACCEPTED",
      "createdAt": "2025-06-02T10:00:00Z",
      "objAnswer": "A"  // 或subAnswer取决于type
    }
  ]
}
```

#### `PATCH /std-answers/{id}`

- **描述**: 更新标准答案状态或分数
- **请求体**: `{"status": "OMITTED"}` 或 `{"score": 9}`

---

## 5. 评估结果模块 (Evaluation Results)

### 5.1. 评估标签 (Evaluation Tags)

#### `POST /evaluation-tags`

- **请求体**:

```json
{
  "dataSetVersion": "1.0",  // 必需：必须存在于versions表
  "evaluationTime": 1,
  "model": "GPT-4-Turbo"
}
```

#### `GET /evaluation-tags`

- **响应**: 包含关联的数据集版本信息

### 5.2. 评估结果 (Evaluation Results)

#### `POST /evaluation-results/import`

- **请求体**:

```json
[
  {
    "evaluationTagId": 1,
    "stdQuestionId": 123,
    "type": "OBJECTIVE",  // 必须与关联的标准问题类型一致
    "content": "LLM生成的答案内容..."
  }
]
```

#### `GET /evaluation-results`

- **参数**:
    - `evaluationTagId` (int, optional): 评估批次筛选
    - `type` (enum, optional): [OBJECTIVE, SUBJECTIVE]
    - `status` (enum, optional): [PENDING, ANALYZED, OMITTED]
    - 标准分页排序参数

#### `GET /evaluation-results/export`

- **描述**: 导出用于分析的数据（标准答案+评估结果对照）
- **参数**:
    - `evaluationTagId` (int, required)
    - `type` (enum, required): [OBJECTIVE, SUBJECTIVE]
    - `format` (enum, optional, default: "json"): [json, csv]
- **响应**: 文件下载

- **内容要求**：包含必须的标注性信息，以及内容性信息  
    - 标注性信息：
        - evaluationId
        - stdQuestionId
        - score
        - type
    - 内容性信息：
        - resultText
        - stdAnswerText

---

## 6. 评估分析模块 (Evaluation Analysis)

### 6.1. 分析标签 (Analysis Tags)

#### `POST /analysis-tags`

- **请求体**:

```json
{
  "evaluationTagId": 1,  // 必需：关联的评估批次
  "analysisTime": 1,
  "model": "ScoringLLM-v1"
}
```

### 6.2. 评估分析 (Evaluation Analysis)

#### `POST /evaluation-analysis/import`

- **请求体**:

```json
[
  {
    "evaluationResultId": 1,
    "analysisTagId": 1,
    "score": 8  // 0-10整数
  }
]
```

- **后端逻辑**: 自动更新evaluation_results表的status为ANALYZED

#### `GET /evaluation-analysis`

- **参数**:
    - `analysisTagId` (int, optional)
    - `minScore`, `maxScore` (int, optional): 分数范围筛选
    - 标准分页排序参数

---

## 数据模型

### 通用响应格式

```json
// 成功响应
{
  "success": true,
  "data": {...},
  "message": "操作成功"
}

// 错误响应
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "请求参数无效",
    "details": ["originalRawQuestionId不能为空"]
  }
}

// 分页响应 (Spring Boot PageImpl)
{
  "content": [...],
  "pageable": {...},
  "totalPages": 5,
  "totalElements": 98,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "size": 20,
  "number": 0,
  "empty": false
}
```

### 关键约束说明

1. **std_questions.originalRawQuestionId**: 必需，建立追溯关系
2. **std_answers.selectedFromCandidateId**: 必需，标准答案必须来自候选答案
3. **candidate_answers -> std_answers**: 通过PATCH candidate-answers触发创建
4. **级联删除**: 删除原始问题时，使用RESTRICT防止删除已有标准问题
5. **状态管理**: 各实体状态变更需要触发相关联实体的状态更新
