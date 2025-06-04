package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Standard question entity
 */
@Entity
@Table(name = "std_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "original_raw_question_id", nullable = false)
    private Integer originalRawQuestionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandardQuestionStatus status = StandardQuestionStatus.WAITING_ANSWERS;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many-to-one relationship with RawQuestion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_raw_question_id", insertable = false, updatable = false)
    private RawQuestion originalRawQuestion;

    // Many-to-many relationship with Version
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "std_question_versions",
        joinColumns = @JoinColumn(name = "std_question_id"),
        inverseJoinColumns = @JoinColumn(name = "version_id")
    )
    private Set<Version> versions;

    // Many-to-many relationship with Tag
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "std_question_tags",
        joinColumns = @JoinColumn(name = "std_question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags;

    // One-to-many relationship with CandidateAnswer
    @OneToMany(mappedBy = "standardQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CandidateAnswer> candidateAnswers;

    // One-to-many relationship with StandardAnswer
    @OneToMany(mappedBy = "standardQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StandardAnswer> standardAnswers;

    // One-to-many relationship with EvaluationResult
    @OneToMany(mappedBy = "standardQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvaluationResult> evaluationResults;
} 