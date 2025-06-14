package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.StandardAnswer;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StandardAnswer entity
 */
@Repository
public interface StandardAnswerRepository extends JpaRepository<StandardAnswer, Long> {

    /**
     * Find standard answers by standard question ID
     */
    Page<StandardAnswer> findByStdQuestionId(Long stdQuestionId, Pageable pageable);

    /**
     * Find standard answers by standard question ID and type
     */
    Page<StandardAnswer> findByStdQuestionIdAndType(Long stdQuestionId, QuestionType type, Pageable pageable);

    /**
     * Find standard answers by type
     */
    Page<StandardAnswer> findByType(QuestionType type, Pageable pageable);

    /**
     * Find standard answers by status
     */
    Page<StandardAnswer> findByStatus(StandardAnswerStatus status, Pageable pageable);

    /**
     * Find standard answers by type and status
     */
    Page<StandardAnswer> findByTypeAndStatus(QuestionType type, StandardAnswerStatus status, Pageable pageable);

    /**
     * Find standard answers by standard question ID and status
     */
    Page<StandardAnswer> findByStdQuestionIdAndStatus(Long stdQuestionId, StandardAnswerStatus status, Pageable pageable);

    /**
     * Find standard answers by standard question ID, type and status
     */
    Page<StandardAnswer> findByStdQuestionIdAndTypeAndStatus(Long stdQuestionId, QuestionType type, StandardAnswerStatus status, Pageable pageable);

    /**
     * Find standard answer by source candidate answer ID
     */
    Optional<StandardAnswer> findBySelectedFromCandidateId(Long candidateAnswerId);

    /**
     * Check if standard answer exists for candidate answer
     */
    boolean existsBySelectedFromCandidateId(Long candidateAnswerId);

    /**
     * Find standard answers with content by standard question ID
     */
    @Query("SELECT sa FROM StandardAnswer sa " +
           "LEFT JOIN FETCH sa.standardAnswerObj sao " +
           "LEFT JOIN FETCH sa.standardAnswerSub sas " +
           "WHERE sa.stdQuestionId = :stdQuestionId")
    List<StandardAnswer> findWithContentByStdQuestionId(@Param("stdQuestionId") Long stdQuestionId);

    /**
     * Find standard answers with content by ID
     */
    @Query("SELECT sa FROM StandardAnswer sa " +
           "LEFT JOIN FETCH sa.standardAnswerObj sao " +
           "LEFT JOIN FETCH sa.standardAnswerSub sas " +
           "WHERE sa.id = :id")
    Optional<StandardAnswer> findWithContentById(@Param("id") Long id);

    /**
     * Find standard questions without standard answers
     */
    @Query("SELECT sq.id FROM StandardQuestion sq " +
           "WHERE sq.type = :type " +
           "AND NOT EXISTS (SELECT 1 FROM StandardAnswer sa WHERE sa.stdQuestionId = sq.id)")
    List<Long> findStandardQuestionIdsWithoutAnswers(@Param("type") QuestionType type);

    /**
     * Count standard answers by status
     */
    @Query("SELECT sa.status, COUNT(sa) FROM StandardAnswer sa GROUP BY sa.status")
    List<Object[]> getStatusStatistics();

    /**
     * Count standard answers by type
     */
    @Query("SELECT sa.type, COUNT(sa) FROM StandardAnswer sa GROUP BY sa.type")
    List<Object[]> getTypeStatistics();

    /**
     * Count standard answers by type and status
     */
    @Query("SELECT sa.type, sa.status, COUNT(sa) FROM StandardAnswer sa GROUP BY sa.type, sa.status")
    List<Object[]> getTypeAndStatusStatistics();

    /**
     * Get average score by type
     */
    @Query("SELECT sa.type, AVG(sa.score) FROM StandardAnswer sa WHERE sa.score IS NOT NULL GROUP BY sa.type")
    List<Object[]> getAverageScoreByType();

    /**
     * Find high score answers (score >= threshold)
     */
    @Query("SELECT sa FROM StandardAnswer sa " +
           "LEFT JOIN FETCH sa.standardAnswerObj sao " +
           "LEFT JOIN FETCH sa.standardAnswerSub sas " +
           "WHERE sa.score >= :threshold AND sa.type = :type " +
           "ORDER BY sa.score DESC")
    List<StandardAnswer> findHighScoreAnswers(@Param("threshold") Integer threshold, @Param("type") QuestionType type);

    /**
     * Count questions with standard answers by type
     */
    @Query("SELECT COUNT(DISTINCT sa.stdQuestionId) FROM StandardAnswer sa WHERE sa.type = :type")
    Long countQuestionsWithAnswersByType(@Param("type") QuestionType type);

    /**
     * Find standard answers by score range
     */
    @Query("SELECT sa FROM StandardAnswer sa " +
           "WHERE sa.score BETWEEN :minScore AND :maxScore " +
           "AND sa.type = :type " +
           "ORDER BY sa.score DESC")
    Page<StandardAnswer> findByScoreRange(@Param("minScore") Integer minScore, 
                                         @Param("maxScore") Integer maxScore, 
                                         @Param("type") QuestionType type, 
                                         Pageable pageable);
} 