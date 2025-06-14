package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.EvaluationResult;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EvaluationResult entity
 */
@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {

    /**
     * Find evaluation results by evaluation tag ID with pagination
     */
    Page<EvaluationResult> findByEvaluationTagId(Long evaluationTagId, Pageable pageable);

    /**
     * Find evaluation results by standard question ID with pagination
     */
    Page<EvaluationResult> findByStdQuestionId(Long stdQuestionId, Pageable pageable);

    /**
     * Find evaluation results by status with pagination
     */
    Page<EvaluationResult> findByStatus(EvaluationResultStatus status, Pageable pageable);

    /**
     * Find evaluation results by question type with pagination
     */
    Page<EvaluationResult> findByType(QuestionType type, Pageable pageable);

    /**
     * Find evaluation results by evaluation tag ID and status with pagination
     */
    Page<EvaluationResult> findByEvaluationTagIdAndStatus(Long evaluationTagId, EvaluationResultStatus status, Pageable pageable);

    /**
     * Find evaluation results by evaluation tag ID and type with pagination
     */
    Page<EvaluationResult> findByEvaluationTagIdAndType(Long evaluationTagId, QuestionType type, Pageable pageable);

    /**
     * Find evaluation results by evaluation tag ID, status and type with pagination
     */
    Page<EvaluationResult> findByEvaluationTagIdAndStatusAndType(Long evaluationTagId, EvaluationResultStatus status, QuestionType type, Pageable pageable);

    /**
     * Find evaluation results by status in list with pagination
     */
    Page<EvaluationResult> findByStatusIn(List<EvaluationResultStatus> statuses, Pageable pageable);

    /**
     * Count evaluation results by evaluation tag ID
     */
    long countByEvaluationTagId(Long evaluationTagId);

    /**
     * Count evaluation results by status
     */
    long countByStatus(EvaluationResultStatus status);

    /**
     * Count evaluation results by evaluation tag ID and status
     */
    long countByEvaluationTagIdAndStatus(Long evaluationTagId, EvaluationResultStatus status);

    /**
     * Count evaluation results by evaluation tag ID and type
     */
    long countByEvaluationTagIdAndType(Long evaluationTagId, QuestionType type);

    /**
     * Check if evaluation result exists by evaluation tag ID and standard question ID
     */
    boolean existsByEvaluationTagIdAndStdQuestionId(Long evaluationTagId, Long stdQuestionId);

    /**
     * Find evaluation result by evaluation tag ID and standard question ID
     */
    Optional<EvaluationResult> findByEvaluationTagIdAndStdQuestionId(Long evaluationTagId, Long stdQuestionId);

    /**
     * Find evaluation results with standard question details
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "JOIN FETCH er.standardQuestion sq " +
           "WHERE er.evaluationTagId = :evaluationTagId " +
           "ORDER BY er.id")
    List<EvaluationResult> findByEvaluationTagIdWithStandardQuestion(@Param("evaluationTagId") Long evaluationTagId);

    /**
     * Find evaluation results with evaluation tag and standard question details
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "JOIN FETCH er.evaluationTag et " +
           "JOIN FETCH er.standardQuestion sq " +
           "WHERE er.evaluationTagId = :evaluationTagId")
    Page<EvaluationResult> findByEvaluationTagIdWithDetails(@Param("evaluationTagId") Long evaluationTagId, Pageable pageable);

    /**
     * Find evaluation results by model name (through evaluation tag)
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "JOIN er.evaluationTag et " +
           "WHERE et.model = :model")
    Page<EvaluationResult> findByModel(@Param("model") String model, Pageable pageable);

    /**
     * Find evaluation results by model name and status
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "JOIN er.evaluationTag et " +
           "WHERE et.model = :model AND er.status = :status")
    Page<EvaluationResult> findByModelAndStatus(@Param("model") String model, @Param("status") EvaluationResultStatus status, Pageable pageable);

    /**
     * Find pending evaluation results (for analysis)
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "WHERE er.status = 'PENDING' " +
           "AND NOT EXISTS (SELECT 1 FROM EvaluationAnalysis ea WHERE ea.evaluationResultId = er.id) " +
           "ORDER BY er.id")
    Page<EvaluationResult> findPendingEvaluationResultsForAnalysis(Pageable pageable);

    /**
     * Statistics: Count results by status for a specific evaluation tag
     */
    @Query("SELECT er.status, COUNT(er) FROM EvaluationResult er " +
           "WHERE er.evaluationTagId = :evaluationTagId " +
           "GROUP BY er.status")
    List<Object[]> countByStatusForEvaluationTag(@Param("evaluationTagId") Long evaluationTagId);

    /**
     * Statistics: Count results by type for a specific evaluation tag
     */
    @Query("SELECT er.type, COUNT(er) FROM EvaluationResult er " +
           "WHERE er.evaluationTagId = :evaluationTagId " +
           "GROUP BY er.type")
    List<Object[]> countByTypeForEvaluationTag(@Param("evaluationTagId") Long evaluationTagId);

    /**
     * Find evaluation results for export with all details
     */
    @Query("SELECT er FROM EvaluationResult er " +
           "JOIN FETCH er.evaluationTag et " +
           "JOIN FETCH er.standardQuestion sq " +
           "WHERE er.evaluationTagId = :evaluationTagId " +
           "ORDER BY sq.id")
    List<EvaluationResult> findForExport(@Param("evaluationTagId") Long evaluationTagId);

    /**
     * Find evaluation results with analysis count
     */
    @Query("SELECT er, COUNT(ea) as analysis_count FROM EvaluationResult er " +
           "LEFT JOIN er.evaluationAnalyses ea " +
           "WHERE er.evaluationTagId = :evaluationTagId " +
           "GROUP BY er.id " +
           "ORDER BY er.id")
    List<Object[]> findWithAnalysisCount(@Param("evaluationTagId") Long evaluationTagId);
} 