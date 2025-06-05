package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Standard answer subjective entity (weak entity)
 */
@Entity
@Table(name = "std_answers_sub")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardAnswerSub {

    @Id
    @Column(name = "std_answer_id")
    private Long stdAnswerId;

    @Column(name = "sub_answer", columnDefinition = "TEXT", nullable = false)
    private String subAnswer;

    // One-to-one relationship with StandardAnswer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_answer_id")
    @MapsId
    private StandardAnswer standardAnswer;
} 