# API Documentation

这是本后端服务的API设计文档  

## API 文档 (LLM评估数据集管理系统)

**基础URL**: `/api/v1` (假设所有API都有一个版本前缀)
**数据格式**: 所有请求和响应体都使用 `application/json` 格式。

---

### 1. 原始问答模块 (Raw Q&A)

此模块用于管理和处理原始的问答数据。

#### 1.1 原始问题 (Raw Questions)

资源路径: `/raw-questions`

- **POST `/raw-questions/import`**
    - **描述**: 批量导入原始问题。
    - **请求参数**:
        - `sourcePlatform` (Query, String, 可选): 来源平台，例如 `stackoverflow`。
    - **请求体**: `multipart/form-data`，上传包含原始问题数据的文件 (如CSV, JSON)。文件格式需要根据 `sourcePlatform` 预定义或在后端处理时进行适配。
        暂时只考虑`stackoverflow`的数据来源格式  
        考虑为CSV格式，源文件格式参考  

        考虑在后端进行适配，将源文件格式转换为`raw_questions`表的所需要的格式  

    - **成功响应**:
        - `201 Created` 或 `202 Accepted` (如果异步处理)
        - 响应体 (示例):

            ```json
            {
              "message": "原始问题导入成功",
              "importedCount": 100,
              "failedCount": 5,
              "errors": [ // 可选，列出错误详情
                {"originalRecord": "...", "error": "..."}
              ]
            }
            ```

    - **错误响应**: `400 Bad Request`, `500 Internal Server Error`

- **GET `/raw-questions`**
    - **描述**: 查询原始问题列表，支持分页、排序和筛选。
    - **请求参数 (Query)**:
        - `page` (Integer, 可选, 默认 `0`): 页码 (0-indexed)。
        - `size` (Integer, 可选, 默认 `20`): 每页数量。
        - `sortBy` (String, 可选, 默认 `id`): 排序字段 (当前仅支持 `id`)。
        - `order` (String, 可选, 默认 `asc`): 排序顺序 (`asc` 或 `desc`)。
        - `status` (String, 可选): 筛选依据，"WAITING_CONVERTED", "CONVERTED", "OMITTED"。
    - **成功响应**:
        - `200 OK`
        - 响应体 (示例):

            ```json
            {
              "content": [
                {
                  "id": 1,
                  "title": "原始问题标题1",
                  "content": "...", // 原始问题内容，在请求列表中不显示
                  // 为了方便起见，减少请求次数，可以考虑content缓存在前端，然后在浮窗详情中显示
                  "source_platform": "stackoverflow",
                  "tags": "java,spring",
                  "post_id": 1001,
                  "score": 10,
                  "status": "WAITING_CONVERTED"
                }
                // ... 更多问题
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 20,
                "sort": { "sorted": true, "unsorted": false, "empty": false },
                "offset": 0,
                "paged": true,
                "unpaged": false
              },
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

    - **错误响应**: `400 Bad Request`, `500 Internal Server Error`

- **GET `/raw-questions/{id}`**  
    这个可能暂时用不上，但是留着  
    - **描述**: 获取单个原始问题详情。
    - **路径参数**:
        - `id` (Integer): 原始问题ID。
    - **成功响应**:
        - `200 OK`
        - 响应体 (示例): (同上列表中的单个对象)  
    - **错误响应**: `404 Not Found`, `500 Internal Server Error`

#### 1.2 原始答案 (Raw Answers)

资源路径: `/raw-answers` 或嵌套在问题下 `/raw-questions/{questionId}/raw-answers`

- **POST `/raw-answers/import`**
    - **描述**: 为指定的原始问题批量导入原始答案。
    - **请求参数 (Query)**:
        - `sourcePlatform` (String, 可选): 来源平台。
    - **请求体**: 会是一个`raw_answers`的CSV文件  
        文件格式参考：  

        其中含有相关联的原始问题ID，在导入时会根据原始问题ID进行关联  

    - **成功响应**:
        - `201 Created`
        - 响应体 (示例): (类似问题导入的响应)
    - **错误响应**: `400 Bad Request`, `404 Not Found` (如果questionId不存在), `500 Internal Server Error`

- **GET `/raw-answers`**  
    类似请求原始问题列表，但是请求的是答案列表  
    - **描述**: 查询原始答案列表。  
    - **请求参数 (Query)**:
        - `page` (Integer, 可选, 默认 `0`): 页码。
        - `size` (Integer, 可选, 默认 `20`): 每页数量。
        - `sortBy` (String, 可选, 默认 `id`): 排序字段 (当前仅支持 `id`)。
        - `order` (String, 可选, 默认 `asc`): 排序顺序。
    - **成功响应**:
        - `200 OK`
        - 响应体 (示例): (类似问题列表的响应，内容为答案对象)
    - **错误响应**: `400 Bad Request`, `404 Not Found`, `500 Internal Server Error`

- **GET `/raw-questions/{questionId}/raw-answers`**  
    这个在问题页面请求相关回答信息用来展示，或者根据问题ID筛选答案时可能有用  
    - **描述**: 查询指定原始问题下的原始答案列表。
    - **路径参数**:
        - `questionId` (Integer): 原始问题ID。
    - **请求参数 (Query)**:
        - `page` (Integer, 可选, 默认 `0`): 页码。
        - `size` (Integer, 可选, 默认 `20`): 每页数量。
        - `sortBy` (String, 可选, 默认 `id`): 排序字段 (当前仅支持 `id`)。
        - `order` (String, 可选, 默认 `asc`): 排序顺序。
    - **成功响应**:
        - `200 OK`
        - 响应体 (示例): (类似问题列表的响应，内容为答案对象)
    - **错误响应**: `400 Bad Request`, `404 Not Found`, `500 Internal Server Error`

- **GET `/raw-answers/{id}`**  
    这个在答案页面请求相关详情用来展示，或者根据答案ID筛选时可能有用  
    - **描述**: 获取单个原始答案详情。
    - **路径参数**:
        - `id` (Integer): 原始答案ID。
    - **成功响应**: `200 OK`
    - **错误响应**: `404 Not Found`, `500 Internal Server Error`

---

### 2. 标准问题模块 (Standard Questions)

此模块管理标准化的问答数据，包括版本和标签。

#### 2.1 版本 (Version)

资源路径: `/versions`

- **POST `/versions`**
    - **描述**: 创建新的数据集版本。
    - **请求体**:

        ```json
        {
          "version": "1.2-alpha" // 对应 DDL version VARCHAR(20)
        }
        ```

    - **成功响应**:
        - `201 Created`
        - 响应体: (包含创建的版本对象)
    - **错误响应**: `400 Bad Request` (如版本号已存在), `500 Internal Server Error`

- **GET `/versions`**  
    在很多需要选择版本号的地方都需要，用于获取所有版本号作为列表并用来选择  
    - **描述**: 获取所有版本列表。
    - **成功响应**: `200 OK`
        - 响应体: `[{"version": "1.0"}, {"version": "1.1"}]`

#### 2.2 标签 (Tags)

资源路径: `/tags`

- **POST `/tags`**  
    - **描述**: 创建新标签。
    - **请求体**:

        ```json
        {
          "tag": "Linux Kernel" // 对应 DDL tags.tag VARCHAR(100)
        }
        ```

    - **成功响应**:
        - `201 Created`
        - 响应体: (包含创建的标签对象)
    - **错误响应**: `400 Bad Request` (如标签已存在), `500 Internal Server Error`

- **GET `/tags`**
    - **描述**: 获取所有标签列表。
    - **成功响应**: `200 OK`
        - 响应体: `[{"tag": "System Calls"}, {"tag": "Memory Management"}]`

#### 2.3 标准问题 (Standard Questions)

资源路径: `/std-questions`

- **POST `/std-questions/import`**
    - **描述**: 批量导入标准问题，需关联原始问题ID和版本ID。
    - **请求体**: JSON 数组 (示例):

        ```json
        [
          {
            "type": "SUBJECTIVE", // OBJECTIVE 或 SUBJECTIVE
            "content": "标准化后的问题内容...",
            "status": "WAITING_ANSWERS", // WAITING_ANSWERS 或 ANSWERED
            "originalRawQuestionId": 1, // 关联原始问题
            "versionIds": ["1.0", "1.1"], // 此问题属于的版本列表
            "tagNames": ["Linux Kernel", "System Calls"] // 此问题的标签列表
          }
        ]
        ```

    - **成功响应**: `201 Created` / `202 Accepted`
    - **错误响应**: `400 Bad Request`, `500 Internal Server Error`

- **GET `/std-questions`**
    - **描述**: 查询标准问题列表。
    - **请求参数 (Query)**:
        - `page`, `size`, `sortBy` (id), `order`
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE', 必选)
        - `status` (ENUM: 'WAITING_ANSWERS', 'ANSWERED', 可选)
        - `version` (String, 可选): 筛选属于特定版本的问题。
        - `tags` (String, 可选): 筛选包含特定标签的问题 (可以是逗号分隔的多个标签，后端处理AND或OR逻辑)。
    - **成功响应**: `200 OK` (响应体结构类似 `/raw-questions` 的分页列表)
        - 单个对象示例:

            ```json
            {
              "id": 1,
              "type": "SUBJECTIVE",
              "content": "...",
              "status": "ANSWERED",
              "created_at": "2025-06-01T10:00:00Z",
              "versions": [{"version": "1.0"}], // 关联的版本
              "tags": [{"tag": "Linux Kernel"}] // 关联的标签
            }
            ```

    - **错误响应**: `400 Bad Request`, `500 Internal Server Error`

- **GET `/std-questions/{id}`**
    - **描述**: 获取单个标准问题详情。
    - **成功响应**: `200 OK`
    - **错误响应**: `404 Not Found`

- **POST `/std-questions/{id}/tags`**
    - **描述**: 为指定标准问题添加标签。
    - **路径参数**: `id` (Integer) - 标准问题ID。
    - **请求体**: `{"tagName": "new_tag"}`
    - **成功响应**: `201 Created` 或 `200 OK`
    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **DELETE `/std-questions/{id}/tags/{tagName}`**
    - **描述**: 从指定标准问题移除标签。
    - **路径参数**: `id` (Integer) - 标准问题ID, `tagName` (String) - 标签名。
    - **成功响应**: `204 No Content`
    - **错误响应**: `404 Not Found`

---

### 3. 候选答案模块 (Candidate Answers)

资源路径: `/candidate-answers` 或嵌套在标准问题下 `/std-questions/{questionId}/candidate-answers`

- **POST `/candidate-answers/import`**
    - **描述**: 为指定的标准问题导入候选答案。
    - **请求参数 (Query)**:
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE', 必选): 题目类型
    - **请求体**: 会是一个`candidate_answers`的CSV文件  
        文件格式参考：  

        其中含有相关联的标准问题ID，在导入时会根据标准问题ID进行关联  
        其中根据type的不同，会将答案内容存入`candidate_answers_obj`或`candidate_answers_sub`表  
    - **成功响应**: `201 Created` / `202 Accepted`
    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **GET `/candidate-answers`**  
    获取候选答案列表，用于选择候选答案  
    - **描述**: 查询候选答案列表。  
    - **请求参数 (Query)**:  
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE', 必选): 题目类型
        - `page` (Integer, 可选, 默认 `0`): 页码。
        - `size` (Integer, 可选, 默认 `20`): 每页数量。
        - `sortBy` (String, 可选, 默认 `id`): `id` 或 `std_question_id` 排序字段。  
        - `order` (String, 可选, 默认 `asc`): 排序顺序。
        - `status` (ENUM: 'PENDING', 'ACCEPTED', 'OMITTED', 可选): 筛选状态
    - **成功响应**: `200 OK` (分页列表)
        - 单个对象示例:

            ```json
            {
                "id": 1,
                "std_question_id": 123,
                "type": "OBJECTIVE",
                "status": "PENDING",
                "obj_answer": "A" // 若type为OBJECTIVE，则有此字段；若为SUBJECTIVE，则为sub_answer
            }
            ```

            处理：需要通过选择性连表查询（具体参考数据库Schema）  

    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **GET `/std-questions/{questionId}/candidate-answers`**
    - **描述**: 查询指定标准问题下的候选答案列表。
    - **路径参数**: `questionId` (Integer)。
    - **请求参数 (Query)**:
        - `page`, `size`
        - `sortBy` (String, 可选, 默认 `id`): `id`, `std_question_id` (此处的`std_question_id`排序意义不大，因为已限定了questionId)
        - `order`
        - `status` (ENUM: 'PENDING', 'ACCEPTED', 'REJECTED')
    - **成功响应**: `200 OK` (分页列表)
        - 单个对象示例可以见上  

    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **PATCH `/candidate-answers/{id}`**
    - **描述**: 更新候选答案状态 (例如，人工判断后标记为 ACCEPTED/REJECTED)。
        如果标记为接受，还需要触发后端逻辑，将该候选答案选入标准答案之中  
    - **路径参数**: `id` (Integer) - 候选答案ID。
    - **请求体**: `{"status": "ACCEPTED"}`
    - **成功响应**: `200 OK`
        - 响应体: (包含更新后的候选答案对象)
    - **错误响应**: `400 Bad Request`, `404 Not Found`

---

### 4. 标准答案模块 (Standard Answers)

资源路径: `/std-answers` 或嵌套 `/std-questions/{questionId}/std-answers`

<!-- - **POST `/std-answers`**
    - **描述**: 创建标准答案，只能从候选答案接受后创建。
    - **请求体**: -->

标准答案数据体：  

```json
{
    "std_question_id": 123,
    "selected_from_candidate_id": 789, // 关联的候选答案ID
    "type": "OBJECTIVE", // 从候选答案或问题带过来
    "score": 10, // 0-10
    "status": "ACCEPTED", // ACCEPTED 或 OMITTED
    // 答案内容 (obj_answer 或 sub_answer) 会从候选答案复制或在后端逻辑中处理
    "obj_answer": "A", // 示例
    // 存储上与type相关的逻辑与候选答案一致
}
```

录入标准答案的逻辑在候选答案更改状态的PATCH请求中，当候选答案被接受时，应当会自动创建标准答案  

- **GET `/std-answers`**
    - **描述**: 查询标准答案列表。
    - **请求参数 (Query)**:
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE', 必选): 题目类型
        - `page`, `size`
        - `sortBy` (String, 可选, 默认 `id`): `id`, `std_question_id`
        - `order` (String, 可选, 默认 `asc`): 排序顺序
        - `status` (ENUM: 'ACCEPTED', 'OMITTED')
    - **成功响应**: `200 OK` (分页列表)

- **GET `/std-questions/{questionId}/std-answers`**
    - **描述**: 查询指定标准问题下的标准答案列表。
    - **路径参数**: `questionId` (Integer)。
    - **请求参数 (Query)**:
        - `page`, `size`
        - `sortBy` (String, 可选, 默认 `id`): `id`, `std_question_id` (此处的`std_question_id`排序意义不大)
        - `order`
        - `status` (ENUM: 'ACCEPTED', 'OMITTED')
    - **成功响应**: `200 OK` (分页列表)
        - 单个对象示例:

            ```json
            {
              "id": 1,
              "std_question_id": 123,
              "type": "OBJECTIVE",
              "score": 10,
              "status": "ACCEPTED",
              "created_at": "2025-06-02T10:00:00Z",
              "selected_from_candidate_id": 789,
              "obj_answer": "A",
            }
            ```

    - **错误响应**: `400 Bad Request`, `404 Not Found`

---

### 5. 评估结果模块 (Evaluation Results)

此模块用于管理LLM对标准问题的评估结果。

#### 5.1 评估批次标签 (Evaluation Tags)

资源路径: `/evaluation-tags`

- **POST `/evaluation-tags`**
    - **描述**: 创建评估批次标签。
    - **请求体**:

        ```json
        {
          "data_set_version": "1.0", // 关联的数据集版本
          // 必须从版本表中获取  
          "evaluation_time": 1,    // 第几次测试
          "model": "GPT-4-Turbo"   // 测试使用的LLM模型
        }
        ```

    - **成功响应**: `201 Created`
        - 响应体: (包含创建的评估批次标签对象)
    - **错误响应**: `400 Bad Request`, `404 Not Found` (如果 `data_set_version` 无效)

- **GET `/evaluation-tags`**
    - **描述**: 获取所有评估批次标签列表。
    - **成功响应**: `200 OK` (列表)

#### 5.2 评估结果 (Evaluation Results)

资源路径: `/evaluation-results`

- **POST `/evaluation-results/import`**
    - **描述**: 批量导入评估结果。
    - **请求体**: JSON 数组 (示例):

        ```json
        [
          {
            // "id" 不需要，会自增
            "evaluation_tag_id": 1, // 关联的评估批次
            // 必须从evaluation_tags表中获取
            "std_question_id": 123, // 关联的标准问题
            "type": "OBJECTIVE", // 应该与关联的标准问题一致
            "content": "LLM生成的答案内容...",
            // "status": "PENDING" // PENDING, ANALYZED, OMITTED // 不需要，因为默认是PENDING
          }
        ]
        ```

    - **成功响应**: `201 Created` / `202 Accepted`
    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **GET `/evaluation-results`**
    - **描述**: 查询评估结果列表。
    - **请求参数 (Query)**:
        - `page`, `size`
        - `sortBy` (String, 可选, 默认 `id`): `id`, `std_question_id`
        - `order`
        - `status` (ENUM: 'PENDING', 'ANALYZED', 'OMITTED')
        - `tag_id` (Integer): 按评估批次ID筛选。
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE')
    - **成功响应**: `200 OK` (分页列表)
        - 单个对象示例:

            ```json
            {
              "id": 1,
              "evaluation_tag_id": 1,
              "std_question_id": 123,
              "content": "LLM生成的答案...",
              "type": "OBJECTIVE",
              "status": "ANALYZED",
            }
            ```

    - **错误响应**: `400 Bad Request`

- **GET `/evaluation-results/{id}`**
    - **描述**: 获取单个评估结果详情。
    - **成功响应**: `200 OK`
    - **错误响应**: `404 Not Found`

- **GET `/evaluation-results/compare`**
    - **描述**: 获取基于某个测试结果集合的标准答案与测试结果用于对照  
        设想是返回一个json格式文件  
    - **请求参数 (Query)**:
        - `evaluation_tag_id` (INT, 必选): 关联的评估批次ID
        - `type` (ENUM: 'OBJECTIVE', 'SUBJECTIVE', 必选): 题目类型
        - `status` (ENUM: 'PENDING', 'ANALYZED', 'OMITTED') 可选，默认`PENDING`，不能是`OMITTED`： 状态
    - **成功响应**: `200 OK`
        - 响应体: (一个文件，包含评估所需的信息，主要是测试答案和标准答案，需要构建成文件格式返回，供前端下载)  
    - **错误响应**: `400 Bad Request`, `404 Not Found`

---

### 6. 评估结果分析模块 (Evaluation Analysis)

此模块用于管理对LLM评估结果的分析（例如打分）。

#### 6.1 分析批次标签 (Analysis Tags)

资源路径: `/analysis-tags`

- **POST `/analysis-tags`**
    - **描述**: 创建分析批次标签。
    - **请求体**:

        ```json
        {
          "evaluation_tag_id": 1, // 关联的原始LLM评估批次
          // 必须从evaluation_tags表中获取
          "analysis_time": 1,     // 第几次分析
          "model": "ScoringLLM-v1" // 用于分析的模型
        }
        ```

    - **成功响应**: `201 Created`
        - 响应体: (包含创建的分析批次标签对象)
    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **GET `/analysis-tags`**
    - **描述**: 获取所有分析批次标签列表。
    - **成功响应**: `200 OK` (列表)

#### 6.2 评估分析 (Evaluation Analysis)

资源路径: `/evaluation-analysis`

- **POST `/evaluation-analysis/import`**
    - **描述**: 批量导入评估分析结果。
    - **请求体**: JSON 数组 (示例):

        ```json
        [
          {
            "evaluation_result_id": 1, // 关联的具体评估结果(LLM的答案)
            "analysis_tag_id": 1,      // 关联的分析批次
            "score": 8,                // 0-10 整数
            "created_at": "2025-06-03T10:00:00Z" // 可选，否则用数据库默认
          }
        ]
        ```

    - **成功响应**: `201 Created` / `202 Accepted`
        - 后端逻辑需要同时更新对应 `evaluation_results` 表的 `has_analysis` 为 `TRUE` 和 `status` 为 `ANALYZED`。
    - **错误响应**: `400 Bad Request`, `404 Not Found`

- **GET `/evaluation-analysis`**
    - **描述**: 查询评估分析结果列表。 (你的需求文档中提到此表可能需要再考虑，但基础查询还是必要的)
    - **请求参数 (Query)**:
        - `page`, `size`, `sortBy` (id), `order`
        - `evaluation_result_id` (Integer, 可选): 按评估结果ID筛选。
        - `analysis_tag_id` (Integer, 可选): 按分析批次ID筛选。
        - `min_score` (Integer, 可选): 最低分。
        - `max_score` (Integer, 可选): 最高分。
    - **成功响应**: `200 OK` (分页列表)
        - 单个对象示例:

            ```json
            {
              "id": 1,
              "evaluation_result_id": 1,
              "analysis_tag_id": 1,
              "score": 8,
              "created_at": "2025-06-03T10:00:00Z"
            }
            ```

    - **错误响应**: `400 Bad Request`

- **GET `/evaluation-analysis/{id}`**
    - **描述**: 获取单个评估分析详情。
    - **成功响应**: `200 OK`
    - **错误响应**: `404 Not Found`

---
