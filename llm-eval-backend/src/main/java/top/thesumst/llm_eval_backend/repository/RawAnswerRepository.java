package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.RawAnswer;

/**
 * Repository interface for RawAnswer entity
 */
@Repository
public interface RawAnswerRepository extends JpaRepository<RawAnswer, Integer> {

    /**
     * Find raw answers by raw question ID with pagination
     */
    Page<RawAnswer> findByRawQuestionId(Integer rawQuestionId, Pageable pageable);

    /**
     * Find raw answers by source platform with pagination
     */
    Page<RawAnswer> findBySourcePlatform(String sourcePlatform, Pageable pageable);

    /**
     * Count answers by raw question ID
     */
    long countByRawQuestionId(Integer rawQuestionId);

    /**
     * Check if a raw answer exists by post ID and source platform
     */
    boolean existsByPostIdAndSourcePlatform(Integer postId, String sourcePlatform);
} 