# 数据访问层设计 (DAO/Repository Layer)

## 简化版Repository设计

基于Spring Data JPA的简化数据访问层，专注核心功能实现，减少复杂查询。

---

## 1. 基础Repository配置

### 1.1 基础Repository接口

```java
// 扩展JpaRepository的基础接口
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    // 批量插入优化
    @Modifying
    @Query("INSERT INTO #{#entityName} SELECT s FROM #{#entityName} s WHERE s.id IN :ids")
    void batchInsert(@Param("ids") List<ID> ids);
    
    // 软删除支持（如果需要）
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id = :id")
    void softDeleteById(@Param("id") ID id);
}

// Repository配置
@EnableJpaRepositories(
    basePackages = "com.llmeval.repository",
    repositoryBaseClass = BaseRepositoryImpl.class
)
@Configuration
public class RepositoryConfig {
    // 配置内容
}
```

---

## 2. 原始问答模块Repository

### 2.1 RawQuestionRepository

```java
@Repository
public interface RawQuestionRepository extends BaseRepository<RawQuestion, Long> {
    
    // 按状态查询
    Page<RawQuestion> findByStatus(RawQuestionStatus status, Pageable pageable);
    
    // 按状态和平台查询
    Page<RawQuestion> findByStatusAndSourcePlatform(
        RawQuestionStatus status, 
        String sourcePlatform, 
        Pageable pageable
    );
    
    // 按平台查询
    Page<RawQuestion> findBySourcePlatform(String sourcePlatform, Pageable pageable);
    
    // 检查postId是否存在（防重复导入）
    boolean existsByPostId(Integer postId);
    
    // 按postId查询
    Optional<RawQuestion> findByPostId(Integer postId);
    
    // 统计各状态数量
    @Query("SELECT r.status, COUNT(r) FROM RawQuestion r GROUP BY r.status")
    List<Object[]> countByStatus();
    
    // 获取已转换为标准问题的原始问题
    @Query("SELECT r FROM RawQuestion r WHERE EXISTS " +
           "(SELECT 1 FROM StandardQuestion sq WHERE sq.originalRawQuestionId = r.id)")
    Page<RawQuestion> findQuestionsWithStandardQuestions(Pageable pageable);
}
```

### 2.2 RawAnswerRepository

```java
@Repository
public interface RawAnswerRepository extends BaseRepository<RawAnswer, Long> {
    
    // 按原始问题ID查询答案
    Page<RawAnswer> findByRawQuestionId(Long rawQuestionId, Pageable pageable);
    
    // 按原始问题ID列表查询答案
    List<RawAnswer> findByRawQuestionIdIn(List<Long> rawQuestionIds);
    
    // 检查答案是否存在（防重复导入）
    boolean existsByPostId(Integer postId);
    
    // 按postId查询
    Optional<RawAnswer> findByPostId(Integer postId);
    
    // 统计某问题的答案数量
    @Query("SELECT COUNT(ra) FROM RawAnswer ra WHERE ra.rawQuestionId = :questionId")
    Long countByRawQuestionId(@Param("questionId") Long questionId);
    
    // 批量删除指定问题的答案
    @Modifying
    @Query("DELETE FROM RawAnswer ra WHERE ra.rawQuestionId = :questionId")
    void deleteByRawQuestionId(@Param("questionId") Long questionId);
}
```

---

## 3. 标准问题模块Repository

### 3.1 VersionRepository

```java
@Repository
public interface VersionRepository extends BaseRepository<Version, String> {
    
    // 按创建时间排序获取所有版本
    List<Version> findAllByOrderByCreatedAtDesc();
    
    // 检查版本是否存在
    boolean existsByVersion(String version);
    
    // 获取最新版本
    @Query("SELECT v FROM Version v ORDER BY v.createdAt DESC LIMIT 1")
    Optional<Version> findLatestVersion();
}
```

### 3.2 TagRepository

```java
@Repository
public interface TagRepository extends BaseRepository<Tag, String> {
    
    // 按标签名查询（忽略大小写）
    Optional<Tag> findByTagIgnoreCase(String tag);
    
    // 批量查询标签
    List<Tag> findByTagIn(List<String> tags);
    
    // 按创建时间排序获取所有标签
    List<Tag> findAllByOrderByCreatedAtDesc();
    
    // 模糊搜索标签
    @Query("SELECT t FROM Tag t WHERE t.tag LIKE %:keyword%")
    List<Tag> findByTagContaining(@Param("keyword") String keyword);
    
    // 统计标签使用次数
    @Query("SELECT t.tag, COUNT(sq) FROM Tag t " +
           "LEFT JOIN t.standardQuestions sq " +
           "GROUP BY t.tag ORDER BY COUNT(sq) DESC")
    List<Object[]> getTagUsageStatistics();
}
```

### 3.3 StandardQuestionRepository

```java
@Repository
public interface StandardQuestionRepository extends BaseRepository<StandardQuestion, Long> {
    
    // 基础查询 - 按类型
    Page<StandardQuestion> findByType(QuestionType type, Pageable pageable);
    
    // 按类型和状态查询
    Page<StandardQuestion> findByTypeAndStatus(
        QuestionType type, 
        QuestionStatus status, 
        Pageable pageable
    );
    
    // 按原始问题ID查询
    Page<StandardQuestion> findByOriginalRawQuestionId(
        Long originalRawQuestionId, 
        Pageable pageable
    );
    
    // 检查原始问题是否已转换
    boolean existsByOriginalRawQuestionId(Long originalRawQuestionId);
    
    // 统计各类型问题数量
    @Query("SELECT sq.type, COUNT(sq) FROM StandardQuestion sq GROUP BY sq.type")
    List<Object[]> countByType();
    
    // 统计各状态问题数量
    @Query("SELECT sq.status, COUNT(sq) FROM StandardQuestion sq GROUP BY sq.status")
    List<Object[]> countByStatus();
    
    // 按版本查询问题
    @Query("SELECT sq FROM StandardQuestion sq JOIN sq.versions v WHERE v.version = :version")
    Page<StandardQuestion> findByVersion(@Param("version") String version, Pageable pageable);
    
    // 按标签查询问题（包含任一标签）
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq JOIN sq.tags t WHERE t.tag IN :tags")
    Page<StandardQuestion> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);
    
    // 复合查询 - 类型+状态+版本
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "LEFT JOIN sq.versions v " +
           "WHERE sq.type = :type " +
           "AND (:status IS NULL OR sq.status = :status) " +
           "AND (:version IS NULL OR v.version = :version)")
    Page<StandardQuestion> findByTypeAndStatusAndVersion(
        @Param("type") QuestionType type,
        @Param("status") QuestionStatus status,
        @Param("version") String version,
        Pageable pageable
    );
    
    // 查询未有标准答案的问题
    @Query("SELECT sq FROM StandardQuestion sq WHERE sq.status = 'WAITING_ANSWERS' " +
           "AND NOT EXISTS (SELECT 1 FROM StandardAnswer sa WHERE sa.stdQuestionId = sq.id)")
    Page<StandardQuestion> findQuestionsWithoutAnswers(Pageable pageable);
}
```

---

## 4. 答案模块Repository

### 4.1 CandidateAnswerRepository

```java
@Repository
public interface CandidateAnswerRepository extends BaseRepository<CandidateAnswer, Long> {
    
    // 按问题ID和类型查询
    Page<CandidateAnswer> findByStdQuestionIdAndType(
        Long stdQuestionId, 
        QuestionType type, 
        Pageable pageable
    );
    
    // 按类型查询
    Page<CandidateAnswer> findByType(QuestionType type, Pageable pageable);
    
    // 按类型和状态查询
    Page<CandidateAnswer> findByTypeAndStatus(
        QuestionType type, 
        AnswerStatus status, 
        Pageable pageable
    );
    
    // 按状态查询
    Page<CandidateAnswer> findByStatus(AnswerStatus status, Pageable pageable);
    
    // 统计某问题的候选答案数量
    Long countByStdQuestionId(Long stdQuestionId);
    
    // 统计各状态的候选答案数量
    @Query("SELECT ca.status, COUNT(ca) FROM CandidateAnswer ca GROUP BY ca.status")
    List<Object[]> countByStatus();
    
    // 查询待处理的候选答案
    @Query("SELECT ca FROM CandidateAnswer ca WHERE ca.status = 'PENDING'")
    Page<CandidateAnswer> findPendingAnswers(Pageable pageable);
    
    // 批量更新状态
    @Modifying
    @Query("UPDATE CandidateAnswer ca SET ca.status = :status WHERE ca.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") AnswerStatus status);
}
```

### 4.2 StandardAnswerRepository

```java
@Repository
public interface StandardAnswerRepository extends BaseRepository<StandardAnswer, Long> {
    
    // 按问题ID和类型查询
    Page<StandardAnswer> findByStdQuestionIdAndType(
        Long stdQuestionId, 
        QuestionType type, 
        Pageable pageable
    );
    
    // 按类型查询
    Page<StandardAnswer> findByType(QuestionType type, Pageable pageable);
    
    // 按类型和状态查询
    Page<StandardAnswer> findByTypeAndStatus(
        QuestionType type, 
        AnswerStatus status, 
        Pageable pageable
    );
    
    // 检查问题是否已有标准答案
    boolean existsByStdQuestionId(Long stdQuestionId);
    
    // 按候选答案ID查询（确保唯一性）
    Optional<StandardAnswer> findBySelectedFromCandidateId(Long candidateId);
    
    // 统计各类型标准答案数量
    @Query("SELECT sa.type, COUNT(sa) FROM StandardAnswer sa GROUP BY sa.type")
    List<Object[]> countByType();
    
    // 获取高分答案
    @Query("SELECT sa FROM StandardAnswer sa WHERE sa.score >= :minScore ORDER BY sa.score DESC")
    Page<StandardAnswer> findHighScoreAnswers(@Param("minScore") Integer minScore, Pageable pageable);
    
    // 统计平均分
    @Query("SELECT AVG(sa.score) FROM StandardAnswer sa WHERE sa.type = :type")
    Double getAverageScoreByType(@Param("type") QuestionType type);
}
```

---

## 5. 评估模块Repository

### 5.1 EvaluationTagRepository

```java
@Repository
public interface EvaluationTagRepository extends BaseRepository<EvaluationTag, Long> {
    
    // 按版本查询评估标签
    Page<EvaluationTag> findByDataSetVersion(String dataSetVersion, Pageable pageable);
    
    // 按模型查询
    Page<EvaluationTag> findByModel(String model, Pageable pageable);
    
    // 按版本和模型查询
    Page<EvaluationTag> findByDataSetVersionAndModel(
        String dataSetVersion, 
        String model, 
        Pageable pageable
    );
    
    // 获取最新的评估标签
    @Query("SELECT et FROM EvaluationTag et ORDER BY et.createdAt DESC")
    Page<EvaluationTag> findLatestEvaluations(Pageable pageable);
    
    // 统计不同模型的评估次数
    @Query("SELECT et.model, COUNT(et) FROM EvaluationTag et GROUP BY et.model")
    List<Object[]> countByModel();
}
```

### 5.2 EvaluationResultRepository

```java
@Repository
public interface EvaluationResultRepository extends BaseRepository<EvaluationResult, Long> {
    
    // 按评估标签查询
    Page<EvaluationResult> findByEvaluationTagId(
        Long evaluationTagId, 
        Pageable pageable
    );
    
    // 按评估标签和类型查询
    Page<EvaluationResult> findByEvaluationTagIdAndType(
        Long evaluationTagId, 
        QuestionType type, 
        Pageable pageable
    );
    
    // 按评估标签、类型和状态查询
    Page<EvaluationResult> findByEvaluationTagIdAndTypeAndStatus(
        Long evaluationTagId, 
        QuestionType type, 
        EvaluationStatus status, 
        Pageable pageable
    );
    
    // 统计评估结果状态
    @Query("SELECT er.status, COUNT(er) FROM EvaluationResult er " +
           "WHERE er.evaluationTagId = :tagId GROUP BY er.status")
    List<Object[]> countByStatusAndTagId(@Param("tagId") Long tagId);
    
    // 检查是否已有评估结果
    boolean existsByEvaluationTagIdAndStdQuestionId(
        Long evaluationTagId, 
        Long stdQuestionId
    );
    
    // 导出数据查询
    @Query("SELECT er, sa FROM EvaluationResult er " +
           "LEFT JOIN StandardAnswer sa ON sa.stdQuestionId = er.stdQuestionId " +
           "WHERE er.evaluationTagId = :tagId AND er.type = :type")
    List<Object[]> findResultsWithAnswers(
        @Param("tagId") Long evaluationTagId, 
        @Param("type") QuestionType type
    );
    
    // 批量更新状态
    @Modifying
    @Query("UPDATE EvaluationResult er SET er.status = :status WHERE er.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") EvaluationStatus status);
}
```

### 5.3 AnalysisTagRepository

```java
@Repository
public interface AnalysisTagRepository extends BaseRepository<AnalysisTag, Long> {
    
    // 按评估标签查询分析标签
    Page<AnalysisTag> findByEvaluationTagId(Long evaluationTagId, Pageable pageable);
    
    // 按分析模型查询
    Page<AnalysisTag> findByModel(String model, Pageable pageable);
    
    // 检查评估标签是否已有分析
    boolean existsByEvaluationTagId(Long evaluationTagId);
    
    // 获取最新分析
    @Query("SELECT at FROM AnalysisTag at ORDER BY at.createdAt DESC")
    Page<AnalysisTag> findLatestAnalysis(Pageable pageable);
}
```

### 5.4 EvaluationAnalysisRepository

```java
@Repository
public interface EvaluationAnalysisRepository extends BaseRepository<EvaluationAnalysis, Long> {
    
    // 按分析标签查询
    Page<EvaluationAnalysis> findByAnalysisTagId(Long analysisTagId, Pageable pageable);
    
    // 按分数范围查询
    @Query("SELECT ea FROM EvaluationAnalysis ea WHERE ea.score BETWEEN :minScore AND :maxScore")
    Page<EvaluationAnalysis> findByScoreRange(
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore,
        Pageable pageable
    );
    
    // 按分析标签和分数范围查询
    @Query("SELECT ea FROM EvaluationAnalysis ea " +
           "WHERE ea.analysisTagId = :tagId " +
           "AND (:minScore IS NULL OR ea.score >= :minScore) " +
           "AND (:maxScore IS NULL OR ea.score <= :maxScore)")
    Page<EvaluationAnalysis> findByTagIdAndScoreRange(
        @Param("tagId") Long analysisTagId,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore,
        Pageable pageable
    );
    
    // 统计分数分布
    @Query("SELECT ea.score, COUNT(ea) FROM EvaluationAnalysis ea " +
           "WHERE ea.analysisTagId = :tagId GROUP BY ea.score ORDER BY ea.score")
    List<Object[]> getScoreDistribution(@Param("tagId") Long analysisTagId);
    
    // 计算平均分
    @Query("SELECT AVG(ea.score) FROM EvaluationAnalysis ea WHERE ea.analysisTagId = :tagId")
    Double getAverageScore(@Param("tagId") Long analysisTagId);
    
    // 检查评估结果是否已分析
    boolean existsByEvaluationResultId(Long evaluationResultId);
}
```

---

## 6. 统计相关Repository

### 6.1 统计查询Repository

```java
@Repository
public interface StatisticsRepository {
    
    // 基本统计信息
    @Query(value = """
        SELECT 
            'rawQuestions' as type, COUNT(*) as count FROM raw_questions
        UNION ALL
        SELECT 
            'rawAnswers' as type, COUNT(*) as count FROM raw_answers
        UNION ALL
        SELECT 
            'standardQuestions' as type, COUNT(*) as count FROM std_questions
        UNION ALL
        SELECT 
            'standardAnswers' as type, COUNT(*) as count FROM std_answers
        UNION ALL
        SELECT 
            'candidateAnswers' as type, COUNT(*) as count FROM candidate_answers
        UNION ALL
        SELECT 
            'evaluationResults' as type, COUNT(*) as count FROM evaluation_results
        """, nativeQuery = true)
    List<Object[]> getBasicStatistics();
    
    // 标签使用统计
    @Query(value = """
        SELECT t.tag, COUNT(sqt.std_question_id) as question_count
        FROM tags t
        LEFT JOIN std_question_tags sqt ON t.tag = sqt.tag_name
        GROUP BY t.tag
        ORDER BY question_count DESC
        """, nativeQuery = true)
    List<Object[]> getTagUsageStatistics();
    
    // 版本问题数统计
    @Query(value = """
        SELECT v.version, COUNT(sqv.std_question_id) as question_count
        FROM version v
        LEFT JOIN std_question_versions sqv ON v.version = sqv.version_id
        GROUP BY v.version
        ORDER BY question_count DESC
        """, nativeQuery = true)
    List<Object[]> getVersionStatistics();
}
```

---

## 简化说明

### 1. 主要简化点：

1. **标准JPA方法**: 优先使用Spring Data JPA的方法命名规则
2. **减少复杂查询**: 避免过度复杂的SQL，提高可维护性
3. **统一分页**: 所有查询都支持分页，避免性能问题
4. **合理索引**: 查询方法对应数据库索引设计

### 2. 性能优化：

1. **懒加载**: 关联查询使用懒加载避免N+1问题
2. **批量操作**: 提供批量更新和删除方法
3. **投影查询**: 统计查询使用原生SQL提高性能
4. **索引友好**: 查询条件对应数据库索引

### 3. 使用示例：

```java
// Service层使用示例
@Service
public class StandardQuestionService {
    
    @Autowired
    private StandardQuestionRepository questionRepository;
    
    public Page<StandardQuestion> findQuestions(StandardQuestionQueryDto query) {
        return questionRepository.findByTypeAndStatusAndVersion(
            query.getType(),
            query.getStatus(),
            query.getVersion(),
            query.toPageable()
        );
    }
}
```

这个设计确保了数据访问层的简洁性和高性能，同时满足所有核心业务需求。 