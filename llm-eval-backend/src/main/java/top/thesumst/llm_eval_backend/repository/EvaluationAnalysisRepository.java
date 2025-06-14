package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.EvaluationAnalysis;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EvaluationAnalysis entity
 */
@Repository
public interface EvaluationAnalysisRepository extends JpaRepository<EvaluationAnalysis, Long> {

    /**
     * Find analysis results by analysis tag ID
     */
    List<EvaluationAnalysis> findByAnalysisTagId(Long analysisTagId);

    /**
     * Find analysis results by analysis tag ID with pagination
     */
    Page<EvaluationAnalysis> findByAnalysisTagId(Long analysisTagId, Pageable pageable);

    /**
     * Find analysis results by evaluation result ID
     */
    List<EvaluationAnalysis> findByEvaluationResultId(Long evaluationResultId);

    /**
     * Find analysis result by evaluation result ID and analysis tag ID
     */
    Optional<EvaluationAnalysis> findByEvaluationResultIdAndAnalysisTagId(Long evaluationResultId, Long analysisTagId);

    /**
     * Count analysis results by analysis tag ID
     */
    long countByAnalysisTagId(Long analysisTagId);

    /**
     * Check if analysis result exists for evaluation result and analysis tag
     */
    boolean existsByEvaluationResultIdAndAnalysisTagId(Long evaluationResultId, Long analysisTagId);

    /**
     * Get score distribution for all analysis results
     */
    @Query("SELECT ea.score, COUNT(ea) " +
           "FROM EvaluationAnalysis ea " +
           "GROUP BY ea.score " +
           "ORDER BY ea.score")
    List<Object[]> getScoreDistribution();

    /**
     * Get score distribution by analysis tag ID
     */
    @Query("SELECT ea.score, COUNT(ea) " +
           "FROM EvaluationAnalysis ea " +
           "WHERE ea.analysisTagId = :analysisTagId " +
           "GROUP BY ea.score " +
           "ORDER BY ea.score")
    List<Object[]> getScoreDistributionByAnalysisTag(@Param("analysisTagId") Long analysisTagId);

    /**
     * Get average scores by analysis model
     */
    @Query("SELECT at.model, AVG(ea.score) " +
           "FROM EvaluationAnalysis ea " +
           "JOIN ea.analysisTag at " +
           "GROUP BY at.model")
    List<Object[]> getAverageScoresByModel();

    /**
     * Get analysis statistics by analysis tag
     */
    @Query("SELECT at.analysisTagId, at.model, COUNT(ea), AVG(ea.score) " +
           "FROM AnalysisTag at " +
           "LEFT JOIN at.evaluationAnalyses ea " +
           "GROUP BY at.analysisTagId, at.model")
    List<Object[]> getAnalysisStatisticsByTag();

    /**
     * Get overall analysis statistics
     */
    @Query("SELECT COUNT(ea), AVG(ea.score), MIN(ea.score), MAX(ea.score) " +
           "FROM EvaluationAnalysis ea")
    Object[] getOverallStatistics();

    /**
     * Get analysis results with detailed information
     */
    @Query("SELECT ea, at.model, er.evaluationTag.model, sq.id, sq.content " +
           "FROM EvaluationAnalysis ea " +
           "JOIN ea.analysisTag at " +
           "JOIN ea.evaluationResult er " +
           "JOIN er.standardQuestion sq " +
           "WHERE ea.analysisTagId = :analysisTagId")
    List<Object[]> findAnalysisResultsWithDetails(@Param("analysisTagId") Long analysisTagId);

    /**
     * Get analysis results with detailed information (paginated)
     */
    @Query("SELECT ea, at.model, er.evaluationTag.model, sq.id, sq.content " +
           "FROM EvaluationAnalysis ea " +
           "JOIN ea.analysisTag at " +
           "JOIN ea.evaluationResult er " +
           "JOIN er.standardQuestion sq " +
           "WHERE ea.analysisTagId = :analysisTagId")
    Page<Object[]> findAnalysisResultsWithDetails(@Param("analysisTagId") Long analysisTagId, Pageable pageable);

    /**
     * Delete analysis results by analysis tag ID
     */
    void deleteByAnalysisTagId(Long analysisTagId);
} 