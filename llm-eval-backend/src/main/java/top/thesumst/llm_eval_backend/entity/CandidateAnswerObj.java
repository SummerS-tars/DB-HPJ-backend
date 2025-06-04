package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;

/**
 * Candidate answer objective entity (weak entity)
 */
@Entity
@Table(name = "candidate_answers_obj")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CandidateAnswerObj {

    @Id
    @Column(name = "candidate_answer_id")
    private Integer candidateAnswerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "obj_answer", nullable = false)
    private ObjectiveAnswer objAnswer;

    // One-to-one relationship with CandidateAnswer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_answer_id")
    @MapsId
    private CandidateAnswer candidateAnswer;
} 