# Raw Answers Import Module Documentation

## 1. 概述 (Overview)

原始答案导入模块允许用户通过CSV文件批量导入原始答案数据到系统中。该模块是LLM评估数据集管理系统的核心功能之一，用于导入来自StackOverflow等平台的原始答案数据。

## 2. 工作流程 (Working Flow)

### 2.1 导入流程图

```
[前端文件选择] → [上传CSV文件] → [后端接收] → [文件验证] → [逐行解析]
                                                       ↓
[保存成功记录] ← [数据入库] ← [数据验证] ← [创建实体对象] ← [CSV行解析]
     ↓                                                   ↓
[统计结果] → [返回导入报告] ← [错误记录收集] ← [记录处理失败]
```

### 2.2 详细工作流程

#### Step 1: 文件上传与基础验证

- 前端通过文件上传组件选择CSV文件
- 后端接收MultipartFile文件对象
- 验证文件是否为空
- 初始化错误收集器和计数器

#### Step 2: CSV文件逐行处理

- 打开文件流，使用BufferedReader读取
- 跳过第一行（CSV头部）
- 逐行处理CSV数据

#### Step 3: 单行数据解析与验证

- 解析CSV行数据到RawAnswerImportRequest对象
- 验证原始问题ID是否存在（外键约束）
- 检查是否存在重复数据（基于postId + sourcePlatform）
- 数据格式验证（数字字段格式等）

#### Step 4: 数据转换与保存

- 使用ModelMapper将DTO转换为Entity
- 保存到数据库
- 更新成功计数器

#### Step 5: 错误处理与统计

- 收集处理失败的记录和错误信息
- 更新失败计数器
- 记录详细错误日志

#### Step 6: 返回导入结果

- 生成导入报告（成功数量、失败数量、错误详情）
- 返回标准API响应格式

## 3. 必需属性与字段顺序 (Required Attributes)

### 3.1 CSV文件格式

**CSV文件头部 (Header):**

```
rawQuestionId,content,postId,score
```

**字段顺序与说明:**

| 序号 | 字段名 | 类型 | 必需 | 描述 | 示例 |
|------|--------|------|------|------|------|
| 1 | rawQuestionId | Long | ✅ | 原始问题ID，必须存在于系统中 | 1 |
| 2 | content | String | ❌ | 答案内容，支持长文本 | "这是一个详细的解答..." |
| 3 | postId | Integer | ❌ | 来源平台的答案ID（如StackOverflow） | 12345 |
| 4 | score | Integer | ❌ | 答案评分/点赞数 | 15 |

**注意事项:**

- `sourcePlatform` 字段通过API参数传递，不在CSV中
- CSV支持引号包围的字段内容，用于处理包含逗号的文本
- 数字字段如果为空或格式错误，会记录警告但不会中断导入

### 3.2 CSV示例文件

```csv
rawQuestionId,content,postId,score
1,"You can use the following approach:\n\nFirst, install the required packages...",67890,25
2,"Another solution would be to use regex:\n\n```python\nimport re\n```",67891,12
3,"I recommend using the built-in methods:",67892,8
```

## 4. API接口文档 (API Documentation)

### 4.1 导入接口

**请求信息:**

```
POST /api/v1/raw-answers/import
Content-Type: multipart/form-data
```

**请求参数:**

| 参数名 | 类型 | 必需 | 描述 |
|--------|------|------|------|
| file | MultipartFile | ✅ | CSV文件 |
| sourcePlatform | String | ❌ | 来源平台（默认：stackoverflow） |

**响应格式:**

```json
{
  "success": true,
  "data": {
    "message": "原始答案导入成功",
    "importedCount": 25,
    "failedCount": 2,
    "errors": [
      {
        "originalRecord": "999,\"Some content\",12345,10",
        "error": "原始问题不存在，ID: 999"
      }
    ]
  },
  "message": "原始答案导入完成"
}
```

### 4.2 查询接口

**获取答案列表:**

```
GET /api/v1/raw-answers?page=0&size=20&sortBy=id&order=asc&sourcePlatform=stackoverflow
```

**获取问题的答案:**

```
GET /api/v1/raw-questions/{questionId}/answers?page=0&size=20
```

## 5. 前端集成指南 (Frontend Integration Guide)

### 5.1 文件上传组件

```javascript
// React示例
const RawAnswerImport = () => {
  const [file, setFile] = useState(null);
  const [sourcePlatform, setSourcePlatform] = useState('stackoverflow');
  const [importing, setImporting] = useState(false);
  
  const handleImport = async () => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('sourcePlatform', sourcePlatform);
    
    setImporting(true);
    try {
      const response = await fetch('/api/v1/raw-answers/import', {
        method: 'POST',
        body: formData,
      });
      
      const result = await response.json();
      if (result.success) {
        showSuccess(`导入成功：${result.data.importedCount}条记录`);
        if (result.data.errors?.length > 0) {
          showWarning(`${result.data.failedCount}条记录导入失败`);
        }
      }
    } catch (error) {
      showError('导入失败：' + error.message);
    } finally {
      setImporting(false);
    }
  };
  
  return (
    <div>
      <input 
        type="file" 
        accept=".csv" 
        onChange={(e) => setFile(e.target.files[0])} 
      />
      <select 
        value={sourcePlatform} 
        onChange={(e) => setSourcePlatform(e.target.value)}
      >
        <option value="stackoverflow">StackOverflow</option>
        <option value="github">GitHub</option>
      </select>
      <button 
        onClick={handleImport} 
        disabled={!file || importing}
      >
        {importing ? '导入中...' : '导入答案'}
      </button>
    </div>
  );
};
```

### 5.2 错误处理

```javascript
const handleImportErrors = (errors) => {
  if (errors && errors.length > 0) {
    const errorList = errors.map(err => ({
      record: err.originalRecord,
      reason: err.error
    }));
    
    // 显示错误详情弹窗或表格
    showErrorDialog({
      title: '导入错误详情',
      errors: errorList
    });
  }
};
```

## 6. 数据验证规则 (Validation Rules)

### 6.1 必需验证

- CSV文件不能为空
- rawQuestionId必须是有效的数字
- rawQuestionId必须在系统中存在

### 6.2 格式验证

- postId必须是有效数字（如果提供）
- score必须是有效数字（如果提供）
- content可以包含任意文本内容

### 6.3 业务验证

- 不允许重复导入（相同postId + sourcePlatform）
- 原始问题必须存在于系统中
- sourcePlatform参数验证

## 7. 错误码与处理 (Error Codes)

| 错误码 | 错误信息 | 处理建议 |
|--------|----------|----------|
| IMPORT_DATA_INVALID | 上传文件不能为空 | 检查文件选择 |
| IMPORT_PARSE_ERROR | 文件解析失败 | 检查CSV格式 |
| RESOURCE_NOT_FOUND | 原始问题不存在 | 检查rawQuestionId |
| - | CSV行格式无效 | 检查字段数量和格式 |
| - | 数字格式错误 | 检查数字字段格式 |

## 8. 性能考虑 (Performance Considerations)

### 8.1 导入性能

- 使用事务处理确保数据一致性
- 逐行处理避免内存溢出
- 建议单次导入不超过10,000条记录

### 8.2 监控指标

- 导入成功率
- 平均处理时间
- 错误类型分布
- 文件大小统计

## 9. 测试用例 (Test Cases)

### 9.1 正常流程测试

- 标准CSV文件导入
- 包含引号和特殊字符的内容
- 部分字段为空的情况

### 9.2 异常流程测试

- 空文件上传
- 格式错误的CSV
- 不存在的rawQuestionId
- 重复数据导入

### 9.3 边界条件测试

- 大文件导入（1万+记录）
- 超长文本内容
- 特殊字符处理

---

**文档版本**: v1.0  
**创建时间**: 当前  
**适用范围**: LLM评估数据集管理系统  
**维护责任**: 后端开发团队
