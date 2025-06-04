# 服务层设计 (Service Layer)

## 简化版Service设计

专注核心业务逻辑的服务层设计，减少复杂性，确保必须功能的可靠实现。

---

## 1. 基础Service配置

### 1.1 基础Service抽象类

```java
@Transactional(readOnly = true)
public abstract class BaseService<T, ID> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected ModelMapper modelMapper;
    
    // 通用分页查询
    protected <R> Page<R> convertPage(Page<T> entityPage, Class<R> dtoClass) {
        return entityPage.map(entity -> modelMapper.map(entity, dtoClass));
    }
    
    // 通用列表转换
    protected <R> List<R> convertList(List<T> entities, Class<R> dtoClass) {
        return entities.stream()
            .map(entity -> modelMapper.map(entity, dtoClass))
            .collect(Collectors.toList());
    }
    
    // 通用实体转换
    protected <R> R convertEntity(T entity, Class<R> dtoClass) {
        return entity != null ? modelMapper.map(entity, dtoClass) : null;
    }
}
```

### 1.2 业务异常定义

```java
// 业务异常基类
public class BusinessException extends RuntimeException {
    private String errorCode;
    private String message;
    private List<String> details;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public BusinessException(String errorCode, String message, List<String> details) {
        this(errorCode, message);
        this.details = details;
    }
}

// 具体业务异常
public class DataValidationException extends BusinessException {
    public DataValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
    
    public DataValidationException(String message, List<String> details) {
        super("VALIDATION_ERROR", message, details);
    }
}

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Object id) {
        super("RESOURCE_NOT_FOUND", String.format("%s with id %s not found", resource, id));
    }
}

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String resource, String field, Object value) {
        super("DUPLICATE_RESOURCE", String.format("%s with %s %s already exists", resource, field, value));
    }
}
```

---

## 2. 原始问答模块Service

### 2.1 RawQuestionService

```java
@Service
@Transactional(readOnly = true)
public class RawQuestionService extends BaseService<RawQuestion, Long> {
    
    @Autowired
    private RawQuestionRepository rawQuestionRepository;
    
    @Autowired
    private StandardQuestionRepository standardQuestionRepository;
    
    // 分页查询原始问题
    public Page<RawQuestionResponseDto> findQuestions(RawQuestionQueryDto query) {
        Page<RawQuestion> questions;
        
        if (query.getStatus() != null && query.getSourcePlatform() != null) {
            questions = rawQuestionRepository.findByStatusAndSourcePlatform(
                query.getStatus(), query.getSourcePlatform(), query.toPageable());
        } else if (query.getStatus() != null) {
            questions = rawQuestionRepository.findByStatus(query.getStatus(), query.toPageable());
        } else if (query.getSourcePlatform() != null) {
            questions = rawQuestionRepository.findBySourcePlatform(query.getSourcePlatform(), query.toPageable());
        } else {
            questions = rawQuestionRepository.findAll(query.toPageable());
        }
        
        return questions.map(this::convertToResponseDto);
    }
    
    // 获取单个原始问题
    public RawQuestionResponseDto findById(Long id) {
        RawQuestion question = rawQuestionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RawQuestion", id));
        return convertToResponseDto(question);
    }
    
    // 批量导入原始问题
    @Transactional
    public ImportResultDto importQuestions(MultipartFile file, String sourcePlatform) {
        List<String> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;
        int rowNumber = 0;
        
        try {
            // 简化处理：假设已经预处理过的CSV/JSON文件
            List<RawQuestionImportData> importData = parseImportFile(file);
            
            for (RawQuestionImportData data : importData) {
                rowNumber++;
                try {
                    // 检查重复
                    if (data.getPostId() != null && rawQuestionRepository.existsByPostId(data.getPostId())) {
                        logger.warn("Duplicate postId: {}", data.getPostId());
                        continue;
                    }
                    
                    RawQuestion question = new RawQuestion();
                    question.setTitle(data.getTitle());
                    question.setContent(data.getContent());
                    question.setSourcePlatform(sourcePlatform);
                    question.setTags(data.getTags());
                    question.setPostId(data.getPostId());
                    question.setScore(data.getScore());
                    
                    rawQuestionRepository.save(question);
                    importedCount++;
                    
                } catch (Exception e) {
                    failedCount++;
                    errors.add(new ImportErrorDto(rowNumber, e.getMessage()).toString());
                }
            }
            
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "导入文件处理失败: " + e.getMessage());
        }
        
        return new ImportResultDto("导入完成", importedCount, failedCount, 
            errors.stream().map(e -> new ImportErrorDto(0, e)).collect(Collectors.toList()));
    }
    
    // 更新原始问题状态
    @Transactional
    public void updateStatus(Long id, RawQuestionStatus status) {
        RawQuestion question = rawQuestionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RawQuestion", id));
        
        question.setStatus(status);
        rawQuestionRepository.save(question);
    }
    
    // 获取从某个原始问题转换的标准问题
    public Page<StandardQuestionResponseDto> findStandardQuestions(Long rawQuestionId, Pageable pageable) {
        if (!rawQuestionRepository.existsById(rawQuestionId)) {
            throw new ResourceNotFoundException("RawQuestion", rawQuestionId);
        }
        
        Page<StandardQuestion> standardQuestions = standardQuestionRepository
            .findByOriginalRawQuestionId(rawQuestionId, pageable);
        
        return convertPage(standardQuestions, StandardQuestionResponseDto.class);
    }
    
    // 私有辅助方法
    private RawQuestionResponseDto convertToResponseDto(RawQuestion question) {
        RawQuestionResponseDto dto = convertEntity(question, RawQuestionResponseDto.class);
        // 计算已转换的标准问题数量
        dto.setStdQuestionCount(
            standardQuestionRepository.existsByOriginalRawQuestionId(question.getId()) ? 1 : 0
        );
        return dto;
    }
    
    private List<RawQuestionImportData> parseImportFile(MultipartFile file) {
        // 简化实现：实际应该根据文件类型解析XML/CSV
        // 这里假设有一个专门的解析器
        return Collections.emptyList(); // 占位实现
    }
}
```

### 2.2 RawAnswerService

```java
@Service
@Transactional(readOnly = true)
public class RawAnswerService extends BaseService<RawAnswer, Long> {
    
    @Autowired
    private RawAnswerRepository rawAnswerRepository;
    
    @Autowired
    private RawQuestionRepository rawQuestionRepository;
    
    // 分页查询原始答案
    public Page<RawAnswerResponseDto> findAnswers(RawAnswerQueryDto query) {
        Page<RawAnswer> answers;
        
        if (query.getRawQuestionId() != null) {
            answers = rawAnswerRepository.findByRawQuestionId(query.getRawQuestionId(), query.toPageable());
        } else {
            answers = rawAnswerRepository.findAll(query.toPageable());
        }
        
        return convertPage(answers, RawAnswerResponseDto.class);
    }
    
    // 获取指定原始问题的答案
    public Page<RawAnswerResponseDto> findAnswersByQuestionId(Long questionId, Pageable pageable) {
        if (!rawQuestionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("RawQuestion", questionId);
        }
        
        Page<RawAnswer> answers = rawAnswerRepository.findByRawQuestionId(questionId, pageable);
        return convertPage(answers, RawAnswerResponseDto.class);
    }
    
    // 批量导入原始答案
    @Transactional
    public ImportResultDto importAnswers(MultipartFile file, String sourcePlatform) {
        List<String> errors = new ArrayList<>();
        int importedCount = 0;
        int failedCount = 0;
        int rowNumber = 0;
        
        try {
            List<RawAnswerImportData> importData = parseImportFile(file);
            
            for (RawAnswerImportData data : importData) {
                rowNumber++;
                try {
                    // 检查重复和父问题存在性
                    if (data.getPostId() != null && rawAnswerRepository.existsByPostId(data.getPostId())) {
                        continue;
                    }
                    
                    // 通过ParentId找到对应的原始问题
                    RawQuestion parentQuestion = rawQuestionRepository.findByPostId(data.getParentId())
                        .orElse(null);
                    
                    if (parentQuestion == null) {
                        errors.add("Row " + rowNumber + ": Parent question not found for postId " + data.getParentId());
                        failedCount++;
                        continue;
                    }
                    
                    RawAnswer answer = new RawAnswer();
                    answer.setRawQuestionId(parentQuestion.getId());
                    answer.setContent(data.getContent());
                    answer.setSourcePlatform(sourcePlatform);
                    answer.setPostId(data.getPostId());
                    answer.setScore(data.getScore());
                    
                    rawAnswerRepository.save(answer);
                    importedCount++;
                    
                } catch (Exception e) {
                    failedCount++;
                    errors.add("Row " + rowNumber + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "导入答案文件处理失败: " + e.getMessage());
        }
        
        return new ImportResultDto("答案导入完成", importedCount, failedCount,
            errors.stream().map(e -> new ImportErrorDto(0, e)).collect(Collectors.toList()));
    }
    
    private List<RawAnswerImportData> parseImportFile(MultipartFile file) {
        // 占位实现
        return Collections.emptyList();
    }
}
```

---

## 3. 标准问题模块Service

### 3.1 VersionService

```java
@Service
@Transactional(readOnly = true)
public class VersionService extends BaseService<Version, String> {
    
    @Autowired
    private VersionRepository versionRepository;
    
    // 创建版本
    @Transactional
    public VersionResponseDto createVersion(VersionCreateDto createDto) {
        if (versionRepository.existsByVersion(createDto.getVersion())) {
            throw new DuplicateResourceException("Version", "version", createDto.getVersion());
        }
        
        Version version = new Version();
        version.setVersion(createDto.getVersion());
        
        Version saved = versionRepository.save(version);
        return convertEntity(saved, VersionResponseDto.class);
    }
    
    // 获取所有版本
    public List<VersionResponseDto> findAllVersions() {
        List<Version> versions = versionRepository.findAllByOrderByCreatedAtDesc();
        return convertList(versions, VersionResponseDto.class);
    }
    
    // 获取最新版本
    public VersionResponseDto findLatestVersion() {
        Version latest = versionRepository.findLatestVersion()
            .orElse(null);
        return convertEntity(latest, VersionResponseDto.class);
    }
}
```

### 3.2 TagService

```java
@Service
@Transactional(readOnly = true)
public class TagService extends BaseService<Tag, String> {
    
    @Autowired
    private TagRepository tagRepository;
    
    // 创建标签
    @Transactional
    public TagResponseDto createTag(TagCreateDto createDto) {
        if (tagRepository.existsById(createDto.getTag())) {
            throw new DuplicateResourceException("Tag", "tag", createDto.getTag());
        }
        
        Tag tag = new Tag();
        tag.setTag(createDto.getTag());
        
        Tag saved = tagRepository.save(tag);
        return convertEntity(saved, TagResponseDto.class);
    }
    
    // 获取所有标签
    public List<TagResponseDto> findAllTags() {
        List<Tag> tags = tagRepository.findAllByOrderByCreatedAtDesc();
        return convertList(tags, TagResponseDto.class);
    }
    
    // 标签搜索
    public List<TagResponseDto> searchTags(String keyword) {
        List<Tag> tags = tagRepository.findByTagContaining(keyword);
        return convertList(tags, TagResponseDto.class);
    }
    
    // 获取标签使用统计
    public List<TagStatisticsDto> getTagUsageStatistics() {
        List<Object[]> results = tagRepository.getTagUsageStatistics();
        return results.stream()
            .map(row -> new TagStatisticsDto((String) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
}
```

### 3.3 StandardQuestionService

```java
@Service
@Transactional(readOnly = true)
public class StandardQuestionService extends BaseService<StandardQuestion, Long> {
    
    @Autowired
    private StandardQuestionRepository standardQuestionRepository;
    
    @Autowired
    private RawQuestionRepository rawQuestionRepository;
    
    @Autowired
    private VersionRepository versionRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    // 分页查询标准问题
    public Page<StandardQuestionResponseDto> findQuestions(StandardQuestionQueryDto query) {
        Page<StandardQuestion> questions = standardQuestionRepository
            .findByTypeAndStatusAndVersion(
                query.getType(),
                query.getStatus(),
                query.getVersion(),
                query.toPageable()
            );
        
        return questions.map(this::convertToResponseDto);
    }
    
    // 获取单个标准问题
    public StandardQuestionResponseDto findById(Long id) {
        StandardQuestion question = standardQuestionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StandardQuestion", id));
        return convertToResponseDto(question);
    }
    
    // 批量导入标准问题
    @Transactional
    public ImportResultDto importQuestions(List<StandardQuestionCreateDto> createDtos) {
        int importedCount = 0;
        int failedCount = 0;
        List<ImportErrorDto> errors = new ArrayList<>();
        
        for (StandardQuestionCreateDto dto : createDtos) {
            try {
                validateAndCreateStandardQuestion(dto);
                importedCount++;
            } catch (Exception e) {
                failedCount++;
                errors.add(new ImportErrorDto(failedCount, e.getMessage()));
            }
        }
        
        return new ImportResultDto("标准问题导入完成", importedCount, failedCount, errors);
    }
    
    // 创建单个标准问题
    @Transactional
    public StandardQuestionResponseDto createQuestion(StandardQuestionCreateDto createDto) {
        StandardQuestion question = validateAndCreateStandardQuestion(createDto);
        return convertToResponseDto(question);
    }
    
    // 添加标签
    @Transactional
    public void addTag(Long questionId, TagManageDto tagDto) {
        StandardQuestion question = standardQuestionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("StandardQuestion", questionId));
        
        Tag tag = tagRepository.findById(tagDto.getTagName())
            .orElseThrow(() -> new ResourceNotFoundException("Tag", tagDto.getTagName()));
        
        question.getTags().add(tag);
        standardQuestionRepository.save(question);
    }
    
    // 删除标签
    @Transactional
    public void removeTag(Long questionId, String tagName) {
        StandardQuestion question = standardQuestionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("StandardQuestion", questionId));
        
        question.getTags().removeIf(tag -> tag.getTag().equals(tagName));
        standardQuestionRepository.save(question);
    }
    
    // 查询未有答案的问题
    public Page<StandardQuestionResponseDto> findQuestionsWithoutAnswers(Pageable pageable) {
        Page<StandardQuestion> questions = standardQuestionRepository.findQuestionsWithoutAnswers(pageable);
        return questions.map(this::convertToResponseDto);
    }
    
    // 获取统计信息
    public List<TypeStatisticsDto> getTypeStatistics() {
        List<Object[]> results = standardQuestionRepository.countByType();
        return results.stream()
            .map(row -> new TypeStatisticsDto((QuestionType) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
    
    // 私有辅助方法
    private StandardQuestion validateAndCreateStandardQuestion(StandardQuestionCreateDto dto) {
        // 验证原始问题存在
        if (!rawQuestionRepository.existsById(dto.getOriginalRawQuestionId())) {
            throw new ResourceNotFoundException("RawQuestion", dto.getOriginalRawQuestionId());
        }
        
        StandardQuestion question = new StandardQuestion();
        question.setOriginalRawQuestionId(dto.getOriginalRawQuestionId());
        question.setType(dto.getType());
        question.setContent(dto.getContent());
        question.setStatus(dto.getStatus());
        
        // 处理版本关联
        Set<Version> versions = new HashSet<>();
        for (String versionId : dto.getVersionIds()) {
            Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version", versionId));
            versions.add(version);
        }
        question.setVersions(versions);
        
        // 处理标签关联
        Set<Tag> tags = new HashSet<>();
        for (String tagName : dto.getTagNames()) {
            Tag tag = tagRepository.findById(tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", tagName));
            tags.add(tag);
        }
        question.setTags(tags);
        
        StandardQuestion saved = standardQuestionRepository.save(question);
        
        // 更新原始问题状态
        updateRawQuestionStatus(dto.getOriginalRawQuestionId());
        
        return saved;
    }
    
    private void updateRawQuestionStatus(Long rawQuestionId) {
        RawQuestion rawQuestion = rawQuestionRepository.findById(rawQuestionId).orElse(null);
        if (rawQuestion != null && rawQuestion.getStatus() == RawQuestionStatus.WAITING_CONVERTED) {
            rawQuestion.setStatus(RawQuestionStatus.CONVERTED);
            rawQuestionRepository.save(rawQuestion);
        }
    }
    
    private StandardQuestionResponseDto convertToResponseDto(StandardQuestion question) {
        StandardQuestionResponseDto dto = convertEntity(question, StandardQuestionResponseDto.class);
        
        // 添加关联信息
        if (question.getRawQuestion() != null) {
            dto.setOriginalRawQuestion(convertEntity(question.getRawQuestion(), RawQuestionSummaryDto.class));
        }
        
        return dto;
    }
}
```

---

## 4. 答案模块Service

### 4.1 CandidateAnswerService

```java
@Service
@Transactional(readOnly = true)
public class CandidateAnswerService extends BaseService<CandidateAnswer, Long> {
    
    @Autowired
    private CandidateAnswerRepository candidateAnswerRepository;
    
    @Autowired
    private StandardQuestionRepository standardQuestionRepository;
    
    @Autowired
    private StandardAnswerService standardAnswerService;
    
    // 分页查询候选答案
    public Page<CandidateAnswerResponseDto> findAnswers(CandidateAnswerQueryDto query) {
        Page<CandidateAnswer> answers;
        
        if (query.getStdQuestionId() != null) {
            answers = candidateAnswerRepository.findByStdQuestionIdAndType(
                query.getStdQuestionId(), query.getType(), query.toPageable());
        } else if (query.getStatus() != null) {
            answers = candidateAnswerRepository.findByTypeAndStatus(
                query.getType(), query.getStatus(), query.toPageable());
        } else {
            answers = candidateAnswerRepository.findByType(query.getType(), query.toPageable());
        }
        
        return convertPage(answers, CandidateAnswerResponseDto.class);
    }
    
    // 获取指定标准问题的候选答案
    public Page<CandidateAnswerResponseDto> findAnswersByQuestionId(Long questionId, QuestionType type, Pageable pageable) {
        if (!standardQuestionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("StandardQuestion", questionId);
        }
        
        Page<CandidateAnswer> answers = candidateAnswerRepository
            .findByStdQuestionIdAndType(questionId, type, pageable);
        return convertPage(answers, CandidateAnswerResponseDto.class);
    }
    
    // 批量导入候选答案
    @Transactional
    public ImportResultDto importAnswers(MultipartFile file, QuestionType type) {
        // 简化实现：假设CSV格式包含std_question_id和answer_content
        int importedCount = 0;
        int failedCount = 0;
        List<ImportErrorDto> errors = new ArrayList<>();
        
        try {
            List<CandidateAnswerImportData> importData = parseImportFile(file);
            
            for (CandidateAnswerImportData data : importData) {
                try {
                    if (!standardQuestionRepository.existsById(data.getStdQuestionId())) {
                        errors.add(new ImportErrorDto(failedCount, "Standard question not found: " + data.getStdQuestionId()));
                        failedCount++;
                        continue;
                    }
                    
                    CandidateAnswer answer = new CandidateAnswer();
                    answer.setStdQuestionId(data.getStdQuestionId());
                    answer.setType(type);
                    answer.setAnswerContent(data.getAnswerContent());
                    answer.setStatus(AnswerStatus.PENDING);
                    
                    candidateAnswerRepository.save(answer);
                    importedCount++;
                    
                } catch (Exception e) {
                    failedCount++;
                    errors.add(new ImportErrorDto(failedCount, e.getMessage()));
                }
            }
            
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "导入候选答案失败: " + e.getMessage());
        }
        
        return new ImportResultDto("候选答案导入完成", importedCount, failedCount, errors);
    }
    
    // 更新候选答案状态
    @Transactional
    public CandidateAnswerResponseDto updateAnswerStatus(Long id, CandidateAnswerUpdateDto updateDto) {
        CandidateAnswer answer = candidateAnswerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CandidateAnswer", id));
        
        answer.setStatus(updateDto.getStatus());
        CandidateAnswer updated = candidateAnswerRepository.save(answer);
        
        // 如果状态设为ACCEPTED，自动创建标准答案
        if (updateDto.getStatus() == AnswerStatus.ACCEPTED) {
            if (updateDto.getScore() == null) {
                throw new DataValidationException("接受候选答案时必须提供分数");
            }
            standardAnswerService.createFromCandidateAnswer(answer, updateDto.getScore());
        }
        
        return convertEntity(updated, CandidateAnswerResponseDto.class);
    }
    
    private List<CandidateAnswerImportData> parseImportFile(MultipartFile file) {
        // 占位实现
        return Collections.emptyList();
    }
}
```

### 4.2 StandardAnswerService

```java
@Service
@Transactional(readOnly = true)
public class StandardAnswerService extends BaseService<StandardAnswer, Long> {
    
    @Autowired
    private StandardAnswerRepository standardAnswerRepository;
    
    @Autowired
    private StandardQuestionRepository standardQuestionRepository;
    
    @Autowired
    private CandidateAnswerRepository candidateAnswerRepository;
    
    // 分页查询标准答案
    public Page<StandardAnswerResponseDto> findAnswers(StandardAnswerQueryDto query) {
        Page<StandardAnswer> answers;
        
        if (query.getStdQuestionId() != null) {
            answers = standardAnswerRepository.findByStdQuestionIdAndType(
                query.getStdQuestionId(), query.getType(), query.toPageable());
        } else if (query.getStatus() != null) {
            answers = standardAnswerRepository.findByTypeAndStatus(
                query.getType(), query.getStatus(), query.toPageable());
        } else {
            answers = standardAnswerRepository.findByType(query.getType(), query.toPageable());
        }
        
        return convertPage(answers, StandardAnswerResponseDto.class);
    }
    
    // 获取指定标准问题的标准答案
    public Page<StandardAnswerResponseDto> findAnswersByQuestionId(Long questionId, QuestionType type, Pageable pageable) {
        if (!standardQuestionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("StandardQuestion", questionId);
        }
        
        Page<StandardAnswer> answers = standardAnswerRepository
            .findByStdQuestionIdAndType(questionId, type, pageable);
        return convertPage(answers, StandardAnswerResponseDto.class);
    }
    
    // 从候选答案创建标准答案
    @Transactional
    public StandardAnswerResponseDto createFromCandidateAnswer(CandidateAnswer candidateAnswer, Integer score) {
        // 检查是否已存在标准答案
        if (standardAnswerRepository.findBySelectedFromCandidateId(candidateAnswer.getId()).isPresent()) {
            throw new DuplicateResourceException("StandardAnswer", "candidateAnswerId", candidateAnswer.getId());
        }
        
        StandardAnswer standardAnswer = new StandardAnswer();
        standardAnswer.setStdQuestionId(candidateAnswer.getStdQuestionId());
        standardAnswer.setSelectedFromCandidateId(candidateAnswer.getId());
        standardAnswer.setType(candidateAnswer.getType());
        standardAnswer.setScore(score);
        standardAnswer.setAnswerContent(candidateAnswer.getAnswerContent());
        standardAnswer.setStatus(AnswerStatus.ACCEPTED);
        
        StandardAnswer saved = standardAnswerRepository.save(standardAnswer);
        
        // 更新标准问题状态
        updateQuestionStatus(candidateAnswer.getStdQuestionId());
        
        return convertEntity(saved, StandardAnswerResponseDto.class);
    }
    
    // 更新标准答案
    @Transactional
    public StandardAnswerResponseDto updateAnswer(Long id, StandardAnswerUpdateDto updateDto) {
        StandardAnswer answer = standardAnswerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StandardAnswer", id));
        
        if (updateDto.getStatus() != null) {
            answer.setStatus(updateDto.getStatus());
        }
        if (updateDto.getScore() != null) {
            answer.setScore(updateDto.getScore());
        }
        
        StandardAnswer updated = standardAnswerRepository.save(answer);
        return convertEntity(updated, StandardAnswerResponseDto.class);
    }
    
    // 获取高分答案
    public Page<StandardAnswerResponseDto> findHighScoreAnswers(Integer minScore, Pageable pageable) {
        Page<StandardAnswer> answers = standardAnswerRepository.findHighScoreAnswers(minScore, pageable);
        return convertPage(answers, StandardAnswerResponseDto.class);
    }
    
    // 获取统计信息
    public List<TypeStatisticsDto> getTypeStatistics() {
        List<Object[]> results = standardAnswerRepository.countByType();
        return results.stream()
            .map(row -> new TypeStatisticsDto((QuestionType) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
    
    // 获取平均分
    public Double getAverageScore(QuestionType type) {
        return standardAnswerRepository.getAverageScoreByType(type);
    }
    
    private void updateQuestionStatus(Long questionId) {
        StandardQuestion question = standardQuestionRepository.findById(questionId).orElse(null);
        if (question != null && question.getStatus() == QuestionStatus.WAITING_ANSWERS) {
            question.setStatus(QuestionStatus.ANSWERED);
            standardQuestionRepository.save(question);
        }
    }
}
```

---

## 5. 评估模块Service (简化版)

### 5.1 EvaluationService

```java
@Service
@Transactional(readOnly = true)
public class EvaluationService extends BaseService<EvaluationResult, Long> {
    
    @Autowired
    private EvaluationTagRepository evaluationTagRepository;
    
    @Autowired
    private EvaluationResultRepository evaluationResultRepository;
    
    @Autowired
    private VersionRepository versionRepository;
    
    // 创建评估标签
    @Transactional
    public EvaluationTagResponseDto createEvaluationTag(EvaluationTagCreateDto createDto) {
        // 验证版本存在
        if (!versionRepository.existsById(createDto.getDataSetVersion())) {
            throw new ResourceNotFoundException("Version", createDto.getDataSetVersion());
        }
        
        EvaluationTag tag = new EvaluationTag();
        tag.setDataSetVersion(createDto.getDataSetVersion());
        tag.setEvaluationTime(createDto.getEvaluationTime());
        tag.setModel(createDto.getModel());
        
        EvaluationTag saved = evaluationTagRepository.save(tag);
        return convertEntity(saved, EvaluationTagResponseDto.class);
    }
    
    // 获取评估标签列表
    public Page<EvaluationTagResponseDto> findEvaluationTags(Pageable pageable) {
        Page<EvaluationTag> tags = evaluationTagRepository.findLatestEvaluations(pageable);
        return convertPage(tags, EvaluationTagResponseDto.class);
    }
    
    // 批量导入评估结果
    @Transactional
    public ImportResultDto importEvaluationResults(List<EvaluationResultCreateDto> createDtos) {
        int importedCount = 0;
        int failedCount = 0;
        List<ImportErrorDto> errors = new ArrayList<>();
        
        for (EvaluationResultCreateDto dto : createDtos) {
            try {
                createEvaluationResult(dto);
                importedCount++;
            } catch (Exception e) {
                failedCount++;
                errors.add(new ImportErrorDto(failedCount, e.getMessage()));
            }
        }
        
        return new ImportResultDto("评估结果导入完成", importedCount, failedCount, errors);
    }
    
    // 创建评估结果
    @Transactional
    public EvaluationResultResponseDto createEvaluationResult(EvaluationResultCreateDto createDto) {
        // 验证评估标签和标准问题存在
        if (!evaluationTagRepository.existsById(createDto.getEvaluationTagId())) {
            throw new ResourceNotFoundException("EvaluationTag", createDto.getEvaluationTagId());
        }
        
        // 检查重复
        if (evaluationResultRepository.existsByEvaluationTagIdAndStdQuestionId(
                createDto.getEvaluationTagId(), createDto.getStdQuestionId())) {
            throw new DuplicateResourceException("EvaluationResult", 
                "evaluationTagId_stdQuestionId", 
                createDto.getEvaluationTagId() + "_" + createDto.getStdQuestionId());
        }
        
        EvaluationResult result = new EvaluationResult();
        result.setEvaluationTagId(createDto.getEvaluationTagId());
        result.setStdQuestionId(createDto.getStdQuestionId());
        result.setType(createDto.getType());
        result.setContent(createDto.getContent());
        result.setStatus(EvaluationStatus.PENDING);
        
        EvaluationResult saved = evaluationResultRepository.save(result);
        return convertEntity(saved, EvaluationResultResponseDto.class);
    }
    
    // 查询评估结果
    public Page<EvaluationResultResponseDto> findEvaluationResults(EvaluationResultQueryDto query) {
        Page<EvaluationResult> results;
        
        if (query.getEvaluationTagId() != null && query.getType() != null && query.getStatus() != null) {
            results = evaluationResultRepository.findByEvaluationTagIdAndTypeAndStatus(
                query.getEvaluationTagId(), query.getType(), query.getStatus(), query.toPageable());
        } else if (query.getEvaluationTagId() != null && query.getType() != null) {
            results = evaluationResultRepository.findByEvaluationTagIdAndType(
                query.getEvaluationTagId(), query.getType(), query.toPageable());
        } else if (query.getEvaluationTagId() != null) {
            results = evaluationResultRepository.findByEvaluationTagId(
                query.getEvaluationTagId(), query.toPageable());
        } else {
            results = evaluationResultRepository.findAll(query.toPageable());
        }
        
        return convertPage(results, EvaluationResultResponseDto.class);
    }
    
    // 导出评估结果
    public List<ExportDataDto> exportEvaluationResults(ExportRequestDto exportRequest) {
        List<Object[]> results = evaluationResultRepository.findResultsWithAnswers(
            exportRequest.getEvaluationTagId(), exportRequest.getType());
        
        return results.stream()
            .map(this::convertToExportData)
            .collect(Collectors.toList());
    }
    
    private ExportDataDto convertToExportData(Object[] row) {
        EvaluationResult result = (EvaluationResult) row[0];
        StandardAnswer answer = (StandardAnswer) row[1];
        
        ExportDataDto exportData = new ExportDataDto();
        exportData.setEvaluationId(result.getId());
        exportData.setStdQuestionId(result.getStdQuestionId());
        exportData.setType(result.getType());
        exportData.setResultText(result.getContent());
        exportData.setStdAnswerText(answer != null ? answer.getAnswerContent() : null);
        exportData.setScore(answer != null ? answer.getScore() : null);
        
        return exportData;
    }
}
```

---

## 6. 统计Service

### 6.1 StatisticsService

```java
@Service
@Transactional(readOnly = true)
public class StatisticsService {
    
    @Autowired
    private StatisticsRepository statisticsRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    // 获取基本统计信息
    public BasicStatisticsDto getBasicStatistics() {
        List<Object[]> results = statisticsRepository.getBasicStatistics();
        
        BasicStatisticsDto statistics = new BasicStatisticsDto();
        for (Object[] row : results) {
            String type = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            
            switch (type) {
                case "rawQuestions" -> statistics.setTotalRawQuestions(count);
                case "rawAnswers" -> statistics.setTotalRawAnswers(count);
                case "standardQuestions" -> statistics.setTotalStandardQuestions(count);
                case "standardAnswers" -> statistics.setTotalStandardAnswers(count);
                case "candidateAnswers" -> statistics.setTotalCandidateAnswers(count);
                case "evaluationResults" -> statistics.setTotalEvaluationResults(count);
            }
        }
        
        return statistics;
    }
    
    // 获取标签使用统计
    public List<TagStatisticsDto> getTagUsageStatistics() {
        List<Object[]> results = statisticsRepository.getTagUsageStatistics();
        return results.stream()
            .map(row -> new TagStatisticsDto((String) row[0], ((Number) row[1]).longValue()))
            .collect(Collectors.toList());
    }
    
    // 获取版本统计
    public List<VersionStatisticsDto> getVersionStatistics() {
        List<Object[]> results = statisticsRepository.getVersionStatistics();
        return results.stream()
            .map(row -> new VersionStatisticsDto((String) row[0], ((Number) row[1]).longValue()))
            .collect(Collectors.toList());
    }
}
```

---

## 简化说明

### 1. 主要简化点：

1. **事务管理简化**: 默认只读事务，写操作明确标注@Transactional
2. **异常处理统一**: 使用业务异常类统一处理错误情况
3. **导入功能简化**: 提供统一的导入结果格式和错误处理
4. **状态管理自动化**: 相关实体状态自动更新

### 2. 核心特性：

1. **数据验证**: 在Service层进行业务逻辑验证
2. **关联处理**: 自动处理实体间的状态同步
3. **统一转换**: 使用ModelMapper统一DTO转换
4. **错误追踪**: 详细的错误信息和日志记录

### 3. 性能考虑：

1. **分页查询**: 所有列表查询都支持分页
2. **懒加载**: 关联数据按需加载
3. **批量操作**: 支持批量导入和处理
4. **缓存友好**: 查询方法设计支持缓存

这个设计确保了业务逻辑的简洁性和可维护性，同时满足所有核心功能需求。 