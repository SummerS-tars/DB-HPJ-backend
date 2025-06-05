package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.RawQuestion;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;

/**
 * Repository interface for RawQuestion entity
 */
@Repository
public interface RawQuestionRepository extends JpaRepository<RawQuestion, Long> {

    /**
     * Find raw questions by status with pagination
     */
    Page<RawQuestion> findByStatus(RawQuestionStatus status, Pageable pageable);

    /**
     * Find raw questions by source platform with pagination
     */
    Page<RawQuestion> findBySourcePlatform(String sourcePlatform, Pageable pageable);

    /**
     * Find raw questions by status and source platform with pagination
     */
    Page<RawQuestion> findByStatusAndSourcePlatform(RawQuestionStatus status, String sourcePlatform, Pageable pageable);

    /**
     * Count questions by status
     */
    long countByStatus(RawQuestionStatus status);

    /**
     * Check if a raw question exists by post ID and source platform
     */
    boolean existsByPostIdAndSourcePlatform(Integer postId, String sourcePlatform);
} 