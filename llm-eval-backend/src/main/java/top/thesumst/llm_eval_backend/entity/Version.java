package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * Version entity for dataset versions
 */
@Entity
@Table(name = "version")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Version {

    @Id
    @Column(length = 20)
    private String version;

    // Many-to-many relationship with StandardQuestion
    @ManyToMany(mappedBy = "versions", fetch = FetchType.LAZY)
    private Set<StandardQuestion> standardQuestions;
} 