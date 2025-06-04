package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluation tag entity for LLM evaluation batches
 */
@Entity
@Table(name = "evaluation_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EvaluationTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "data_set_version", length = 50)
    private String dataSetVersion;

    @Column(name = "evaluation_time")
    private Integer evaluationTime;

    @Column(nullable = false, length = 100)
    private String model;

    // Many-to-one relationship with Version
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set_version", insertable = false, updatable = false)
    private Version version;

    // One-to-many relationship with EvaluationResult
    @OneToMany(mappedBy = "evaluationTag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvaluationResult> evaluationResults;

    // One-to-many relationship with AnalysisTag
    @OneToMany(mappedBy = "evaluationTag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisTag> analysisTags;
} 