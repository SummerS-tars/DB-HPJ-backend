package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;

import java.time.LocalDateTime;

/**
 * Standard answer entity
 */
@Entity
@Table(name = "std_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "std_question_id", nullable = false)
    private Integer stdQuestionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    private Integer score; // 0-10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandardAnswerStatus status = StandardAnswerStatus.ACCEPTED;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "selected_from_candidate_id", nullable = false)
    private Integer selectedFromCandidateId;

    // Many-to-one relationship with StandardQuestion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;

    // Many-to-one relationship with CandidateAnswer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_from_candidate_id", insertable = false, updatable = false)
    private CandidateAnswer selectedFromCandidate;

    // One-to-one relationship with StandardAnswerObj
    @OneToOne(mappedBy = "standardAnswer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StandardAnswerObj standardAnswerObj;

    // One-to-one relationship with StandardAnswerSub
    @OneToOne(mappedBy = "standardAnswer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StandardAnswerSub standardAnswerSub;
} 