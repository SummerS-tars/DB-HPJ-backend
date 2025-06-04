# 数据传输对象设计 (DTO Layer)

## 简化版DTO设计

专注于核心功能的DTO设计，减少复杂性，支持必须实现的API功能。

---

## 1. 通用DTO基类

### 1.1 分页请求基类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest {
    @Min(0)
    private int page = 0;
    
    @Min(1)
    @Max(100)
    private int size = 20;
    
    private String sortBy = "id";
    
    @Pattern(regexp = "asc|desc", message = "排序方向只能是asc或desc")
    private String sortDirection = "asc";
    
    // 转换为Spring Data的Pageable
    public Pageable toPageable() {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
```

### 1.2 通用响应包装类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorInfo error;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "操作成功", null);
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, null, new ErrorInfo("BUSINESS_ERROR", message, null));
    }
    
    public static <T> ApiResponse<T> error(String code, String message, List<String> details) {
        return new ApiResponse<>(false, null, null, new ErrorInfo(code, message, details));
    }
}

@Data
@AllArgsConstructor
public class ErrorInfo {
    private String code;
    private String message;
    private List<String> details;
}
```

---

## 2. 原始问答模块DTO

### 2.1 原始问题相关DTO

```java
// 原始问题查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class RawQuestionQueryDto extends BasePageRequest {
    private RawQuestionStatus status;
    private String sourcePlatform;
    
    // 支持的排序字段
    @Override
    public Pageable toPageable() {
        // 限制允许的排序字段
        Set<String> allowedSortFields = Set.of("id", "score", "title", "createdAt");
        String sortField = allowedSortFields.contains(getSortBy()) ? getSortBy() : "id";
        
        Sort.Direction direction = "desc".equalsIgnoreCase(getSortDirection()) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(getPage(), getSize(), Sort.by(direction, sortField));
    }
}

// 原始问题响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawQuestionResponseDto {
    private Long id;
    private String title;
    private String content;
    private String sourcePlatform;
    private String tags;
    private Integer postId;
    private Integer score;
    private RawQuestionStatus status;
    private LocalDateTime createdAt;
    private Integer stdQuestionCount; // 已转换的标准问题数量
}

// 导入结果DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDto {
    private String message;
    private Integer importedCount;
    private Integer failedCount;
    private List<ImportErrorDto> errors;
}

@Data
@AllArgsConstructor
public class ImportErrorDto {
    private Integer row;
    private String error;
}
```

### 2.2 原始答案相关DTO

```java
// 原始答案查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class RawAnswerQueryDto extends BasePageRequest {
    private Long rawQuestionId; // 按问题筛选
}

// 原始答案响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawAnswerResponseDto {
    private Long id;
    private Long rawQuestionId;
    private String content;
    private String sourcePlatform;
    private Integer postId;
    private Integer score;
    private RawQuestionResponseDto rawQuestion; // 关联的原始问题信息（可选）
}
```

---

## 3. 标准问题模块DTO

### 3.1 版本和标签DTO

```java
// 版本创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionCreateDto {
    @NotBlank(message = "版本号不能为空")
    @Size(max = 20, message = "版本号长度不能超过20")
    private String version;
}

// 版本响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionResponseDto {
    private String version;
    private LocalDateTime createdAt;
}

// 标签创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateDto {
    @NotBlank(message = "标签不能为空")
    @Size(max = 100, message = "标签长度不能超过100")
    private String tag;
}

// 标签响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDto {
    private String tag;
    private LocalDateTime createdAt;
}
```

### 3.2 标准问题DTO

```java
// 标准问题创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardQuestionCreateDto {
    @NotNull(message = "原始问题ID不能为空")
    private Long originalRawQuestionId;
    
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    @NotBlank(message = "问题内容不能为空")
    private String content;
    
    private QuestionStatus status = QuestionStatus.WAITING_ANSWERS;
    
    private List<String> versionIds = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
}

// 标准问题查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class StandardQuestionQueryDto extends BasePageRequest {
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    private QuestionStatus status;
    private String version;
    private String tags; // 逗号分隔的标签
    private Long originalRawQuestionId;
}

// 标准问题响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardQuestionResponseDto {
    private Long id;
    private Long originalRawQuestionId;
    private QuestionType type;
    private String content;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    
    private List<VersionResponseDto> versions;
    private List<TagResponseDto> tags;
    private RawQuestionSummaryDto originalRawQuestion; // 原始问题摘要信息
}

// 原始问题摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawQuestionSummaryDto {
    private Long id;
    private String title;
    private String sourcePlatform;
}

// 标签管理DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagManageDto {
    @NotBlank(message = "标签名不能为空")
    private String tagName;
}
```

---

## 4. 答案模块DTO

### 4.1 候选答案DTO

```java
// 候选答案查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class CandidateAnswerQueryDto extends BasePageRequest {
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    private AnswerStatus status;
    private Long stdQuestionId;
}

// 候选答案响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnswerResponseDto {
    private Long id;
    private Long stdQuestionId;
    private QuestionType type;
    private AnswerStatus status;
    private String answerContent;
    private StandardQuestionSummaryDto standardQuestion; // 关联的标准问题摘要
}

// 标准问题摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardQuestionSummaryDto {
    private Long id;
    private QuestionType type;
    private String content;
    private QuestionStatus status;
}

// 候选答案状态更新DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnswerUpdateDto {
    @NotNull(message = "状态不能为空")
    private AnswerStatus status;
    
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 10, message = "分数不能大于10")
    private Integer score; // 接受时必需
}
```

### 4.2 标准答案DTO

```java
// 标准答案查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class StandardAnswerQueryDto extends BasePageRequest {
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    private AnswerStatus status;
    private Long stdQuestionId;
}

// 标准答案响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardAnswerResponseDto {
    private Long id;
    private Long stdQuestionId;
    private Long selectedFromCandidateId;
    private QuestionType type;
    private Integer score;
    private AnswerStatus status;
    private LocalDateTime createdAt;
    private String answerContent;
    
    private StandardQuestionSummaryDto standardQuestion;
    private CandidateAnswerSummaryDto candidateAnswer;
}

// 候选答案摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnswerSummaryDto {
    private Long id;
    private String answerContent;
}

// 标准答案更新DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardAnswerUpdateDto {
    private AnswerStatus status;
    
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 10, message = "分数不能大于10")
    private Integer score;
}
```

---

## 5. 评估模块DTO

### 5.1 评估标签DTO

```java
// 评估标签创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTagCreateDto {
    @NotBlank(message = "数据集版本不能为空")
    private String dataSetVersion;
    
    private Integer evaluationTime;
    
    @NotBlank(message = "模型名称不能为空")
    private String model;
}

// 评估标签响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTagResponseDto {
    private Long tagId;
    private String dataSetVersion;
    private Integer evaluationTime;
    private String model;
    private LocalDateTime createdAt;
    private VersionResponseDto version; // 关联的版本信息
}
```

### 5.2 评估结果DTO

```java
// 评估结果创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultCreateDto {
    @NotNull(message = "评估标签ID不能为空")
    private Long evaluationTagId;
    
    @NotNull(message = "标准问题ID不能为空")
    private Long stdQuestionId;
    
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    private String content;
}

// 评估结果查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationResultQueryDto extends BasePageRequest {
    private Long evaluationTagId;
    private QuestionType type;
    private EvaluationStatus status;
}

// 评估结果响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultResponseDto {
    private Long id;
    private Long evaluationTagId;
    private Long stdQuestionId;
    private String content;
    private QuestionType type;
    private EvaluationStatus status;
    private LocalDateTime createdAt;
    
    private EvaluationTagSummaryDto evaluationTag;
    private StandardQuestionSummaryDto standardQuestion;
}

// 评估标签摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTagSummaryDto {
    private Long tagId;
    private String dataSetVersion;
    private String model;
    private Integer evaluationTime;
}

// 导出请求DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequestDto {
    @NotNull(message = "评估标签ID不能为空")
    private Long evaluationTagId;
    
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    @Pattern(regexp = "json|csv", message = "格式只能是json或csv")
    private String format = "json";
}
```

### 5.3 评估分析DTO

```java
// 分析标签创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTagCreateDto {
    @NotNull(message = "评估标签ID不能为空")
    private Long evaluationTagId;
    
    private Integer analysisTime;
    
    @NotBlank(message = "分析模型不能为空")
    private String model;
}

// 分析标签响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTagResponseDto {
    private Long analysisTagId;
    private Long evaluationTagId;
    private Integer analysisTime;
    private String model;
    private LocalDateTime createdAt;
    private EvaluationTagSummaryDto evaluationTag;
}

// 评估分析创建请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalysisCreateDto {
    @NotNull(message = "评估结果ID不能为空")
    private Long evaluationResultId;
    
    @NotNull(message = "分析标签ID不能为空")
    private Long analysisTagId;
    
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 10, message = "分数不能大于10")
    private Integer score;
}

// 评估分析查询请求
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationAnalysisQueryDto extends BasePageRequest {
    private Long analysisTagId;
    private Integer minScore;
    private Integer maxScore;
}

// 评估分析响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalysisResponseDto {
    private Long id;
    private Long evaluationResultId;
    private Long analysisTagId;
    private Integer score;
    private LocalDateTime createdAt;
    
    private AnalysisTagSummaryDto analysisTag;
    private EvaluationResultSummaryDto evaluationResult;
}

// 分析标签摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTagSummaryDto {
    private Long analysisTagId;
    private String model;
    private Integer analysisTime;
}

// 评估结果摘要DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultSummaryDto {
    private Long id;
    private String content;
    private QuestionType type;
}
```

---

## 6. 统计和工具DTO

### 6.1 统计DTO

```java
// 基本统计响应DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicStatisticsDto {
    private Long totalRawQuestions;
    private Long totalRawAnswers;
    private Long totalStandardQuestions;
    private Long totalStandardAnswers;
    private Long totalCandidateAnswers;
    private Long totalEvaluationResults;
}

// 分类统计DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeStatisticsDto {
    private QuestionType type;
    private Long count;
}

// 标签统计DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagStatisticsDto {
    private String tagName;
    private Long questionCount;
}

// 状态统计DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusStatisticsDto {
    private String status;
    private Long count;
}
```

---

## 简化说明

### 1. 主要简化点：

1. **去除过度验证**: 只保留关键的业务验证规则
2. **简化继承**: 统一使用BasePageRequest处理分页
3. **减少嵌套**: 避免过度的DTO嵌套，使用摘要DTO
4. **实用优先**: 专注于实际使用场景的DTO设计

### 2. 核心特性：

1. **统一响应格式**: ApiResponse包装所有响应
2. **标准分页处理**: BasePageRequest统一分页逻辑
3. **合理验证**: 使用Bean Validation进行参数校验
4. **摘要信息**: 通过Summary DTO减少数据传输

### 3. 使用示例：

```java
// 查询标准问题
StandardQuestionQueryDto query = new StandardQuestionQueryDto();
query.setType(QuestionType.OBJECTIVE);
query.setPage(0);
query.setSize(20);
query.setSortBy("createdAt");
query.setSortDirection("desc");

// 返回统一格式
ApiResponse<Page<StandardQuestionResponseDto>> response = 
    ApiResponse.success(questionService.findAll(query));
```

这个设计平衡了功能完整性和开发效率，专注于必须实现的核心功能。 