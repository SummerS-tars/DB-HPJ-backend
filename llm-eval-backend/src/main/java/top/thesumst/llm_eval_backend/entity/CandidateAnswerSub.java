package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Candidate answer subjective entity (weak entity)
 */
@Entity
@Table(name = "candidate_answers_sub")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"candidateAnswer"})
public class CandidateAnswerSub {

    @Id
    @Column(name = "candidate_answer_id")
    private Long candidateAnswerId;

    @Column(name = "sub_answer", columnDefinition = "TEXT", nullable = false)
    private String subAnswer;

    // One-to-one relationship with CandidateAnswer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_answer_id")
    @MapsId
    private CandidateAnswer candidateAnswer;
} 