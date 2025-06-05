package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.Version;

import java.util.List;

/**
 * Repository interface for Version entity
 */
@Repository
public interface VersionRepository extends JpaRepository<Version, String> {

    /**
     * Find all versions ordered by creation date descending
     */
    List<Version> findAllByOrderByCreatedAtDesc();

    /**
     * Check if version exists
     */
    boolean existsByVersion(String version);

    /**
     * Get version with question count
     */
    @Query("SELECT v, COUNT(sq) FROM Version v LEFT JOIN v.standardQuestions sq GROUP BY v")
    List<Object[]> findAllWithQuestionCount();
} 