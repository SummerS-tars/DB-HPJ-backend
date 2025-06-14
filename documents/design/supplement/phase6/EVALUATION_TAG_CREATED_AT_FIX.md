# Evaluation Tag CreatedAt Field Fix Documentation

## 问题描述 (Problem Description)

评估标签API响应中缺少 `createdAt` 时间戳字段，前端无法显示评估标签的创建时间。

### 原始响应 (Original Response)
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "tagId": 1,
                "dataSetVersion": "v1.0",
                "evaluationTime": 1,
                "model": "thesumst-114514",
                "resultCount": 0,
                "versionName": null
                // 缺少 createdAt 字段
            }
        ]
    }
}
```

## 问题根因分析 (Root Cause Analysis)

### 1. 实体层缺失
- `EvaluationTag` 实体缺少 `createdAt` 字段
- 其他实体（如 `StandardQuestion`）已正确实现此字段

### 2. DTO层缺失
- `EvaluationTagResponse` DTO 缺少 `createdAt` 字段定义
- ModelMapper 无法映射不存在的字段

## 解决方案 (Solution)

### 1. 修复实体层
**文件**: `EvaluationTag.java`

**添加导入:**
```java
import java.time.LocalDateTime;
```

**添加字段:**
```java
@Column(name = "created_at")
private LocalDateTime createdAt = LocalDateTime.now();
```

### 2. 修复DTO层
**文件**: `EvaluationTagResponse.java`

**添加导入:**
```java
import java.time.LocalDateTime;
```

**添加字段:**
```java
private LocalDateTime createdAt;
```

## 修复后效果 (Expected Result)

### 预期响应格式
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "tagId": 1,
                "dataSetVersion": "v1.0",
                "evaluationTime": 1,
                "model": "thesumst-114514",
                "createdAt": "2024-06-14T22:57:25",
                "resultCount": 0,
                "versionName": null
            }
        ]
    }
}
```

## 技术实现细节 (Technical Implementation)

### 字段配置说明
- **字段类型**: `LocalDateTime`
- **数据库列名**: `created_at`
- **默认值**: `LocalDateTime.now()` - 实体创建时自动设置
- **可空性**: 不可为空
- **JSON序列化**: 自动转换为ISO 8601格式字符串

### 与现有模式保持一致
此修复遵循系统中其他实体的相同模式：
```java
// StandardQuestion.java 中的实现
@Column(name = "created_at")
private LocalDateTime createdAt = LocalDateTime.now();
```

## 数据库影响 (Database Impact)

### 新建表
对于新创建的 `evaluation_tags` 表，`created_at` 列将被正确创建。

### 现有数据
如果表已存在但缺少 `created_at` 列，需要执行数据库迁移：
```sql
ALTER TABLE evaluation_tags 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
```

## 测试验证 (Testing & Verification)

### 1. 创建测试
```bash
POST /api/v1/evaluation-tags
{
  "dataSetVersion": "v1.0",
  "evaluationTime": 1234567890,
  "model": "test-model"
}
```

### 2. 查询验证
```bash
GET /api/v1/evaluation-tags?page=0&size=20
```

### 3. 预期验证结果
- 响应包含 `createdAt` 字段
- `createdAt` 值为有效的时间戳
- 格式为 ISO 8601 字符串（例如：`2024-06-14T22:57:25`）

## 前端集成影响 (Frontend Impact)

### 显示创建时间
```javascript
// React 组件示例
const EvaluationTagItem = ({ tag }) => (
  <div>
    <h3>{tag.model}</h3>
    <p>版本: {tag.dataSetVersion}</p>
    <p>创建时间: {new Date(tag.createdAt).toLocaleString()}</p>
  </div>
);
```

### 排序功能
```javascript
// 按创建时间排序
const sortByCreatedAt = (tags, order = 'desc') => {
  return tags.sort((a, b) => {
    const timeA = new Date(a.createdAt).getTime();
    const timeB = new Date(b.createdAt).getTime();
    return order === 'desc' ? timeB - timeA : timeA - timeB;
  });
};
```

## 向后兼容性 (Backward Compatibility)

### API兼容性
- ✅ 不破坏现有API结构
- ✅ 只是新增字段，不修改现有字段
- ✅ 前端可选择性使用新字段

### 数据库兼容性
- ✅ 新建表自动包含字段
- ⚠️ 现有表需要迁移脚本
- ✅ 默认值确保数据完整性

## 相关改进建议 (Future Improvements)

### 1. 统一时间戳模式
建议为所有实体添加标准的审计字段：
```java
@CreationTimestamp
@Column(name = "created_at")
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

### 2. 基础实体类
考虑创建包含通用时间戳字段的基础实体类：
```java
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

---

**修复状态**: ✅ 已完成  
**影响范围**: EvaluationTag 相关功能  
**测试状态**: 待验证  
**文档版本**: v1.0 