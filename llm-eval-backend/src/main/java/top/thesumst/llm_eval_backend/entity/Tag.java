package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * Tag entity for standard question tags
 */
@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Tag {

    @Id
    @Column(length = 100)
    private String tag;

    // Many-to-many relationship with StandardQuestion
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<StandardQuestion> standardQuestions;
} 