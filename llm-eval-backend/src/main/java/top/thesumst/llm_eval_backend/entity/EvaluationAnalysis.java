package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evaluation analysis entity
 */
@Entity
@Table(name = "evaluation_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EvaluationAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_result_id", nullable = false)
    private Long evaluationResultId;

    @Column(name = "analysis_tag_id", nullable = false)
    private Long analysisTagId;

    private Integer score; // 0-10

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many-to-one relationship with EvaluationResult
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_result_id", insertable = false, updatable = false)
    private EvaluationResult evaluationResult;

    // Many-to-one relationship with AnalysisTag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_tag_id", insertable = false, updatable = false)
    private AnalysisTag analysisTag;
} 