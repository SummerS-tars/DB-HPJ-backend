package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.Tag;

import java.util.List;

/**
 * Repository interface for Tag entity
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    /**
     * Find all tags ordered by tag name
     */
    List<Tag> findAllByOrderByTag();

    /**
     * Check if tag exists
     */
    boolean existsByTag(String tag);

    /**
     * Get tags with question count
     */
    @Query("SELECT t, COUNT(sq) FROM Tag t LEFT JOIN t.standardQuestions sq GROUP BY t")
    List<Object[]> findAllWithQuestionCount();

    /**
     * Find tags by name containing (for search)
     */
    List<Tag> findByTagContainingIgnoreCase(String tagName);
} 