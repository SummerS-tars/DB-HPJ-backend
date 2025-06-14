package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.StandardQuestion;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StandardQuestion entity
 */
@Repository
public interface StandardQuestionRepository extends JpaRepository<StandardQuestion, Long> {

    /**
     * Find standard questions by type with pagination
     */
    Page<StandardQuestion> findByType(QuestionType type, Pageable pageable);

    /**
     * Find standard questions by type and status with pagination
     */
    Page<StandardQuestion> findByTypeAndStatus(QuestionType type, StandardQuestionStatus status, Pageable pageable);

    /**
     * Find standard questions by original raw question ID
     */
    Page<StandardQuestion> findByOriginalRawQuestionId(Long originalRawQuestionId, Pageable pageable);

    /**
     * Find standard questions by version
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "JOIN FETCH sq.versions v " +
           "LEFT JOIN FETCH sq.tags " +
           "WHERE v.version = :version")
    Page<StandardQuestion> findByVersion(@Param("version") String version, Pageable pageable);

    /**
     * Find standard questions by tag
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "JOIN FETCH sq.tags t " +
           "LEFT JOIN FETCH sq.versions " +
           "WHERE t.tag = :tag")
    Page<StandardQuestion> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Find standard questions by multiple tags (containing all tags)
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "JOIN FETCH sq.tags t " +
           "LEFT JOIN FETCH sq.versions " +
           "WHERE t.tag IN :tags " +
           "GROUP BY sq " +
           "HAVING COUNT(DISTINCT t.tag) = :tagCount")
    Page<StandardQuestion> findByAllTags(@Param("tags") String[] tags, @Param("tagCount") long tagCount, Pageable pageable);

    /**
     * Complex query with multiple filters
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "LEFT JOIN FETCH sq.versions v " +
           "LEFT JOIN FETCH sq.tags t " +
           "WHERE (:type IS NULL OR sq.type = :type) " +
           "AND (:status IS NULL OR sq.status = :status) " +
           "AND (:version IS NULL OR v.version = :version) " +
           "AND (:originalRawQuestionId IS NULL OR sq.originalRawQuestionId = :originalRawQuestionId) " +
           "AND (:tag IS NULL OR t.tag = :tag)")
    Page<StandardQuestion> findByFilters(@Param("type") QuestionType type,
                                       @Param("status") StandardQuestionStatus status,
                                       @Param("version") String version,
                                       @Param("originalRawQuestionId") Long originalRawQuestionId,
                                       @Param("tag") String tag,
                                       Pageable pageable);

    /**
     * Check if a standard question already exists for the given raw question
     */
    boolean existsByOriginalRawQuestionId(Long originalRawQuestionId);

    /**
     * Count standard questions by status
     */
    long countByStatus(StandardQuestionStatus status);

    /**
     * Count standard questions by type
     */
    long countByType(QuestionType type);

    /**
     * Find standard question by ID with all relationships
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "LEFT JOIN FETCH sq.versions " +
           "LEFT JOIN FETCH sq.tags " +
           "WHERE sq.id = :id")
    Optional<StandardQuestion> findByIdWithRelationships(@Param("id") Long id);

    /**
     * Find standard questions with their answers for export
     */
    @Query("SELECT DISTINCT sq FROM StandardQuestion sq " +
           "LEFT JOIN FETCH sq.versions v " +
           "LEFT JOIN FETCH sq.tags t " +
           "LEFT JOIN FETCH sq.standardAnswers sa " +
           "LEFT JOIN FETCH sa.standardAnswerObj sao " +
           "LEFT JOIN FETCH sa.standardAnswerSub sas " +
           "WHERE (:type IS NULL OR sq.type = :type) " +
           "AND (:version IS NULL OR v.version = :version) " +
           "AND (:tag IS NULL OR t.tag = :tag) " +
           "AND sa.status = 'ACCEPTED' " +
           "ORDER BY sq.id")
    List<StandardQuestion> findQuestionsWithAnswersForExport(@Param("type") QuestionType type,
                                                            @Param("version") String version,
                                                            @Param("tag") String tag);
} 