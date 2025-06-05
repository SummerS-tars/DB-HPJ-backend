package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Analysis tag entity for evaluation analysis batches
 */
@Entity
@Table(name = "analysis_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AnalysisTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_tag_id")
    private Long analysisTagId;

    @Column(name = "evaluation_tag_id", nullable = false)
    private Long evaluationTagId;

    @Column(name = "analysis_time")
    private Integer analysisTime;

    @Column(nullable = false, length = 100)
    private String model;

    // Many-to-one relationship with EvaluationTag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_tag_id", insertable = false, updatable = false)
    private EvaluationTag evaluationTag;

    // One-to-many relationship with EvaluationAnalysis
    @OneToMany(mappedBy = "analysisTag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvaluationAnalysis> evaluationAnalyses;
} 