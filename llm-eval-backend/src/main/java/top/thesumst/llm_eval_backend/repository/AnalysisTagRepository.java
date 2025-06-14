package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.AnalysisTag;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalysisTag entity
 */
@Repository
public interface AnalysisTagRepository extends JpaRepository<AnalysisTag, Long> {

    /**
     * Find analysis tags by evaluation tag ID
     */
    List<AnalysisTag> findByEvaluationTagId(Long evaluationTagId);

    /**
     * Find analysis tags by model name
     */
    List<AnalysisTag> findByModel(String model);

    /**
     * Find analysis tags by evaluation tag ID and model
     */
    Optional<AnalysisTag> findByEvaluationTagIdAndModel(Long evaluationTagId, String model);

    /**
     * Find analysis tags with pagination
     */
    Page<AnalysisTag> findAll(Pageable pageable);

    /**
     * Count analysis tags by evaluation tag ID
     */
    long countByEvaluationTagId(Long evaluationTagId);

    /**
     * Get analysis tags with analysis count
     */
    @Query("SELECT at, COUNT(ea) as analysisCount " +
           "FROM AnalysisTag at " +
           "LEFT JOIN at.evaluationAnalyses ea " +
           "GROUP BY at.analysisTagId")
    List<Object[]> findAnalysisTagsWithCount();

    /**
     * Get analysis tags by evaluation tag ID with analysis count
     */
    @Query("SELECT at, COUNT(ea) as analysisCount " +
           "FROM AnalysisTag at " +
           "LEFT JOIN at.evaluationAnalyses ea " +
           "WHERE at.evaluationTagId = :evaluationTagId " +
           "GROUP BY at.analysisTagId")
    List<Object[]> findAnalysisTagsWithCountByEvaluationTagId(@Param("evaluationTagId") Long evaluationTagId);

    /**
     * Check if analysis tag exists by evaluation tag ID and model
     */
    boolean existsByEvaluationTagIdAndModel(Long evaluationTagId, String model);
} 