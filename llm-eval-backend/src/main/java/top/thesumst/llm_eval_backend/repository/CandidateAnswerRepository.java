package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.CandidateAnswer;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;

import java.util.List;

/**
 * Repository for CandidateAnswer entity
 */
@Repository
public interface CandidateAnswerRepository extends JpaRepository<CandidateAnswer, Long> {

    /**
     * Find all candidate answers by standard question ID
     */
    List<CandidateAnswer> findByStdQuestionId(Long stdQuestionId);

    /**
     * Find candidate answers by standard question ID with pagination
     */
    Page<CandidateAnswer> findByStdQuestionId(Long stdQuestionId, Pageable pageable);

    /**
     * Find candidate answers by status
     */
    Page<CandidateAnswer> findByStatus(CandidateAnswerStatus status, Pageable pageable);

    /**
     * Find candidate answers by type
     */
    Page<CandidateAnswer> findByType(QuestionType type, Pageable pageable);

    /**
     * Find candidate answers by type and status
     */
    Page<CandidateAnswer> findByTypeAndStatus(QuestionType type, CandidateAnswerStatus status, Pageable pageable);

    /**
     * Find candidate answers by standard question ID and status
     */
    Page<CandidateAnswer> findByStdQuestionIdAndStatus(Long stdQuestionId, CandidateAnswerStatus status, Pageable pageable);

    /**
     * Find candidate answers by standard question ID and type
     */
    Page<CandidateAnswer> findByStdQuestionIdAndType(Long stdQuestionId, QuestionType type, Pageable pageable);

    /**
     * Find candidate answers by standard question ID, type and status
     */
    Page<CandidateAnswer> findByStdQuestionIdAndTypeAndStatus(Long stdQuestionId, QuestionType type, CandidateAnswerStatus status, Pageable pageable);

    /**
     * Count by status
     */
    long countByStatus(CandidateAnswerStatus status);

    /**
     * Count by type
     */
    long countByType(QuestionType type);

    /**
     * Count by type and status
     */
    long countByTypeAndStatus(QuestionType type, CandidateAnswerStatus status);

    /**
     * Count by standard question ID
     */
    long countByStdQuestionId(Long stdQuestionId);

    /**
     * Check if candidate answer exists for standard question
     */
    boolean existsByStdQuestionId(Long stdQuestionId);

    /**
     * Find candidate answers with content (with joins)
     */
    @Query("SELECT ca FROM CandidateAnswer ca " +
           "LEFT JOIN FETCH ca.candidateAnswerObj " +
           "LEFT JOIN FETCH ca.candidateAnswerSub " +
           "LEFT JOIN FETCH ca.standardQuestion " +
           "WHERE ca.type = :type AND ca.status = :status")
    Page<CandidateAnswer> findWithContentByTypeAndStatus(@Param("type") QuestionType type, 
                                                         @Param("status") CandidateAnswerStatus status, 
                                                         Pageable pageable);

    /**
     * Find candidate answers with content by standard question ID
     */
    @Query("SELECT ca FROM CandidateAnswer ca " +
           "LEFT JOIN FETCH ca.candidateAnswerObj " +
           "LEFT JOIN FETCH ca.candidateAnswerSub " +
           "LEFT JOIN FETCH ca.standardQuestion " +
           "WHERE ca.stdQuestionId = :stdQuestionId")
    List<CandidateAnswer> findWithContentByStdQuestionId(@Param("stdQuestionId") Long stdQuestionId);

    /**
     * Get statistics by status
     */
    @Query("SELECT ca.status, COUNT(ca) FROM CandidateAnswer ca GROUP BY ca.status")
    List<Object[]> getStatusStatistics();

    /**
     * Get statistics by type
     */
    @Query("SELECT ca.type, COUNT(ca) FROM CandidateAnswer ca GROUP BY ca.type")
    List<Object[]> getTypeStatistics();

    /**
     * Get statistics by type and status
     */
    @Query("SELECT ca.type, ca.status, COUNT(ca) FROM CandidateAnswer ca GROUP BY ca.type, ca.status")
    List<Object[]> getTypeAndStatusStatistics();
} 