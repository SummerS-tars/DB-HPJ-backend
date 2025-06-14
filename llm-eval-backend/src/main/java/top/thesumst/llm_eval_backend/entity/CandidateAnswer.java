package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;

import java.util.List;

/**
 * Candidate answer entity
 */
@Entity
@Table(name = "candidate_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CandidateAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "std_question_id", nullable = false)
    private Long stdQuestionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateAnswerStatus status = CandidateAnswerStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Many-to-one relationship with StandardQuestion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_question_id", insertable = false, updatable = false)
    private StandardQuestion standardQuestion;

    // One-to-one relationship with CandidateAnswerObj
    @OneToOne(mappedBy = "candidateAnswer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CandidateAnswerObj candidateAnswerObj;

    // One-to-one relationship with CandidateAnswerSub
    @OneToOne(mappedBy = "candidateAnswer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CandidateAnswerSub candidateAnswerSub;

    // One-to-many relationship with StandardAnswer (when this candidate is selected)
    @OneToMany(mappedBy = "selectedFromCandidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StandardAnswer> standardAnswers;
} 