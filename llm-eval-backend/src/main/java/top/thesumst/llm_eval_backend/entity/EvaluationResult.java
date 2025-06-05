package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;

import java.util.List;

/**
 * Evaluation result entity
 */
@Entity
@Table(name = "evaluation_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
    @Column(nullable = false)
    private EvaluationResultStatus status = EvaluationResultStatus.PENDING;

    // Many-to-one relationship with EvaluationTag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_tag_id", insertable = false, updatable = false)
    private EvaluationTag evaluationTag;

    // Many-to-one relationship with StandardQuestion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;

    // One-to-many relationship with EvaluationAnalysis
    @OneToMany(mappedBy = "evaluationResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvaluationAnalysis> evaluationAnalyses;
} 