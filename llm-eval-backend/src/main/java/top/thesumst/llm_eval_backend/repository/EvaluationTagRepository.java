package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.EvaluationTag;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EvaluationTag entity
 */
@Repository
public interface EvaluationTagRepository extends JpaRepository<EvaluationTag, Long> {

    /**
     * Find evaluation tags by model name with pagination
     */
    Page<EvaluationTag> findByModel(String model, Pageable pageable);

    /**
     * Find evaluation tags by data set version with pagination
     */
    Page<EvaluationTag> findByDataSetVersion(String dataSetVersion, Pageable pageable);

    /**
     * Find evaluation tags by model and data set version with pagination
     */
    Page<EvaluationTag> findByModelAndDataSetVersion(String model, String dataSetVersion, Pageable pageable);

    /**
     * Find evaluation tags by model containing (case insensitive) with pagination
     */
    Page<EvaluationTag> findByModelContainingIgnoreCase(String model, Pageable pageable);

    /**
     * Check if evaluation tag exists by model and data set version
     */
    boolean existsByModelAndDataSetVersion(String model, String dataSetVersion);

    /**
     * Find latest evaluation tags for each model
     */
    @Query("SELECT et FROM EvaluationTag et " +
           "WHERE et.tagId IN (SELECT MAX(et2.tagId) FROM EvaluationTag et2 GROUP BY et2.model) " +
           "ORDER BY et.tagId DESC")
    Page<EvaluationTag> findLatestEvaluationsByModel(Pageable pageable);

    /**
     * Find all evaluation tags for a specific model ordered by tag ID desc
     */
    Page<EvaluationTag> findByModelOrderByTagIdDesc(String model, Pageable pageable);

    /**
     * Count evaluation tags by model
     */
    long countByModel(String model);

    /**
     * Count evaluation tags by data set version
     */
    long countByDataSetVersion(String dataSetVersion);

    /**
     * Find all unique models
     */
    @Query("SELECT DISTINCT et.model FROM EvaluationTag et ORDER BY et.model")
    List<String> findAllUniqueModels();

    /**
     * Find all unique data set versions
     */
    @Query("SELECT DISTINCT et.dataSetVersion FROM EvaluationTag et ORDER BY et.dataSetVersion")
    List<String> findAllUniqueDataSetVersions();

    /**
     * Find evaluation tags with evaluation results count
     */
    @Query("SELECT et, COUNT(er) as result_count FROM EvaluationTag et " +
           "LEFT JOIN et.evaluationResults er " +
           "GROUP BY et.tagId " +
           "ORDER BY et.tagId DESC")
    Page<Object[]> findEvaluationTagsWithResultCount(Pageable pageable);

    /**
     * Find evaluation tags by model with evaluation results count
     */
    @Query("SELECT et, COUNT(er) as result_count FROM EvaluationTag et " +
           "LEFT JOIN et.evaluationResults er " +
           "WHERE et.model = :model " +
           "GROUP BY et.tagId " +
           "ORDER BY et.tagId DESC")
    Page<Object[]> findEvaluationTagsWithResultCountByModel(@Param("model") String model, Pageable pageable);
} 