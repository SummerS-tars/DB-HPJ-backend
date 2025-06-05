package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import top.thesumst.llm_eval_backend.entity.enums.ObjectiveAnswer;

/**
 * Standard answer objective entity (weak entity)
 */
@Entity
@Table(name = "std_answers_obj")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardAnswerObj {

    @Id
    @Column(name = "std_answer_id")
    private Long stdAnswerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "obj_answer", nullable = false)
    private ObjectiveAnswer objAnswer;

    // One-to-one relationship with StandardAnswer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_answer_id")
    @MapsId
    private StandardAnswer standardAnswer;
} 