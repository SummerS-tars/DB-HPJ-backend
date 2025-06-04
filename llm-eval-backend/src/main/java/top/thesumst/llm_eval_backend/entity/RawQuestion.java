package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;

import java.util.List;

/**
 * Raw question entity
 */
@Entity
@Table(name = "raw_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RawQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RawQuestionStatus status = RawQuestionStatus.WAITING_CONVERTED;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "source_platform", length = 100)
    private String sourcePlatform;

    @Column(length = 255)
    private String tags;

    @Column(name = "post_id")
    private Integer postId;

    private Integer score;

    // One-to-many relationship with RawAnswer
    @OneToMany(mappedBy = "rawQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RawAnswer> rawAnswers;

    // One-to-many relationship with StandardQuestion
    @OneToMany(mappedBy = "originalRawQuestion", fetch = FetchType.LAZY)
    private List<StandardQuestion> standardQuestions;
} 