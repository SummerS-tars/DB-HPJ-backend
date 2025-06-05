package top.thesumst.llm_eval_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Many-to-many relationship with StandardQuestion
    @ManyToMany(mappedBy = "versions", fetch = FetchType.LAZY)
    private Set<StandardQuestion> standardQuestions;
} 