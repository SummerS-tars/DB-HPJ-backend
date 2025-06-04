# 数据模型设计 (Entity Layer)

## 简化版实体设计

基于必须实现功能的简化实体设计，减少复杂性，专注核心需求。

---

## 1. 原始问答模块实体

### 1.1 RawQuestion (原始问题)

```java
@Entity
@Table(name = "raw_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "source_platform")
    private String sourcePlatform = "stackoverflow";
    
    private String tags;  // 逗号分隔的标签字符串，简化处理
    
    @Column(name = "post_id")
    private Integer postId;
    
    private Integer score;
    
    @Enumerated(EnumType.STRING)
    private RawQuestionStatus status = RawQuestionStatus.WAITING_CONVERTED;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 方便查询的关联
    @OneToMany(mappedBy = "rawQuestion", fetch = FetchType.LAZY)
    private List<StandardQuestion> standardQuestions = new ArrayList<>();
}

enum RawQuestionStatus {
    WAITING_CONVERTED, CONVERTED, OMITTED
}
```

### 1.2 RawAnswer (原始答案)

```java
@Entity
@Table(name = "raw_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "raw_question_id", nullable = false)
    private Long rawQuestionId;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "source_platform")
    private String sourcePlatform = "stackoverflow";
    
    @Column(name = "post_id")
    private Integer postId;
    
    private Integer score;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_question_id", insertable = false, updatable = false)
    private RawQuestion rawQuestion;
}
```

---

## 2. 标准问题模块实体

### 2.1 Version (版本)

```java
@Entity
@Table(name = "version")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Version {
    @Id
    @Column(length = 20)
    private String version;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

### 2.2 Tag (标签)

```java
@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @Column(length = 100)
    private String tag;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

### 2.3 StandardQuestion (标准问题)

```java
@Entity
@Table(name = "std_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "original_raw_question_id", nullable = false)
    private Long originalRawQuestionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private QuestionStatus status = QuestionStatus.WAITING_ANSWERS;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_raw_question_id", insertable = false, updatable = false)
    private RawQuestion rawQuestion;
    
    // 多对多关系 - 版本
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "std_question_versions",
        joinColumns = @JoinColumn(name = "std_question_id"),
        inverseJoinColumns = @JoinColumn(name = "version_id")
    )
    private Set<Version> versions = new HashSet<>();
    
    // 多对多关系 - 标签
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "std_question_tags",
        joinColumns = @JoinColumn(name = "std_question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags = new HashSet<>();
}

enum QuestionType {
    OBJECTIVE, SUBJECTIVE
}

enum QuestionStatus {
    WAITING_ANSWERS, ANSWERED
}
```

---

## 3. 答案模块实体 (简化版)

### 3.1 CandidateAnswer (候选答案)

```java
@Entity
@Table(name = "candidate_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "std_question_id", nullable = false)
    private Long stdQuestionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    private AnswerStatus status = AnswerStatus.PENDING;
    
    // 简化处理：直接在主表存储答案内容，根据type判断
    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String answerContent;  // 客观题存选项，主观题存文本
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;
}

enum AnswerStatus {
    PENDING, ACCEPTED, REJECTED
}
```

### 3.2 StandardAnswer (标准答案)

```java
@Entity
@Table(name = "std_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "std_question_id", nullable = false)
    private Long stdQuestionId;
    
    @Column(name = "selected_from_candidate_id", nullable = false)
    private Long selectedFromCandidateId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Column(nullable = false)
    @Min(0) @Max(10)
    private Integer score;
    
    @Enumerated(EnumType.STRING)
    private AnswerStatus status = AnswerStatus.ACCEPTED;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 简化处理：直接存储答案内容
    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String answerContent;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_from_candidate_id", insertable = false, updatable = false)
    private CandidateAnswer candidateAnswer;
}
```

---

## 4. 评估模块实体

### 4.1 EvaluationTag (评估标签)

```java
@Entity
@Table(name = "evaluation_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;
    
    @Column(name = "data_set_version", length = 50)
    private String dataSetVersion;
    
    @Column(name = "evaluation_time")
    private Integer evaluationTime;
    
    @Column(length = 100, nullable = false)
    private String model;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set_version", insertable = false, updatable = false)
    private Version version;
}
```

### 4.2 EvaluationResult (评估结果)

```java
@Entity
@Table(name = "evaluation_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "evaluation_tag_id", nullable = false)
    private Long evaluationTagId;
    
    @Column(name = "std_question_id", nullable = false)
    private Long stdQuestionId;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    private EvaluationStatus status = EvaluationStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_tag_id", insertable = false, updatable = false)
    private EvaluationTag evaluationTag;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;
}

enum EvaluationStatus {
    PENDING, ANALYZED, OMITTED
}
```

### 4.3 AnalysisTag (分析标签)

```java
@Entity
@Table(name = "analysis_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_tag_id")
    private Long analysisTagId;
    
    @Column(name = "evaluation_tag_id", nullable = false)
    private Long evaluationTagId;
    
    @Column(name = "analysis_time")
    private Integer analysisTime;
    
    @Column(length = 100, nullable = false)
    private String model;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_tag_id", insertable = false, updatable = false)
    private EvaluationTag evaluationTag;
}
```

### 4.4 EvaluationAnalysis (评估分析)

```java
@Entity
@Table(name = "evaluation_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "evaluation_result_id", nullable = false)
    private Long evaluationResultId;
    
    @Column(name = "analysis_tag_id", nullable = false)
    private Long analysisTagId;
    
    @Column(nullable = false)
    @Min(0) @Max(10)
    private Integer score;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_result_id", insertable = false, updatable = false)
    private EvaluationResult evaluationResult;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_tag_id", insertable = false, updatable = false)
    private AnalysisTag analysisTag;
}
```

---

## 简化说明

### 1. 主要简化点：

1. **答案存储简化**: 取消了弱实体表，直接在主表存储答案内容
2. **标签处理简化**: RawQuestion的tags直接用字符串存储
3. **审计简化**: 只保留创建时间，取消更新时间
4. **关系简化**: 保留必要的关联关系，去除复杂的级联操作

### 2. 保留的核心功能：

1. **必要约束**: originalRawQuestionId和selectedFromCandidateId的NOT NULL约束
2. **关系追溯**: 标准问题到原始问题的关联
3. **版本管理**: 多对多关系处理数据集版本
4. **状态管理**: 各实体的状态枚举

### 3. 枚举定义：

```java
// 集中定义的枚举类
public enum QuestionType {
    OBJECTIVE, SUBJECTIVE
}

public enum QuestionStatus {
    WAITING_ANSWERS, ANSWERED
}

public enum RawQuestionStatus {
    WAITING_CONVERTED, CONVERTED, OMITTED
}

public enum AnswerStatus {
    PENDING, ACCEPTED, REJECTED
}

public enum EvaluationStatus {
    PENDING, ANALYZED, OMITTED
}
```

### 4. 约束和验证：

- 使用Bean Validation注解进行基础校验
- 外键约束通过数据库层面保证
- 业务逻辑约束在Service层处理

这个简化版本去除了不必要的复杂性，专注于核心功能实现，便于快速开发和维护。 