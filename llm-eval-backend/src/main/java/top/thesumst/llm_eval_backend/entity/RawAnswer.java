package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Raw answer entity
 */
@Entity
@Table(name = "raw_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RawAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "raw_question_id", nullable = false)
    private Integer rawQuestionId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "source_platform", length = 100)
    private String sourcePlatform;

    @Column(name = "post_id")
    private Integer postId;

    private Integer score;

    // Many-to-one relationship with RawQuestion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_question_id", insertable = false, updatable = false)
    private RawQuestion rawQuestion;
} 