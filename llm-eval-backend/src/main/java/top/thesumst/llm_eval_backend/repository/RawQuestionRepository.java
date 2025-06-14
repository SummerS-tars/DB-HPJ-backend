package top.thesumst.llm_eval_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.thesumst.llm_eval_backend.entity.RawQuestion;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;

import java.util.List;
import java.util.Optional;

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

    /**
     * Find raw question by post ID
     */
    Optional<RawQuestion> findByPostId(Integer postId);

    /**
     * Find StackOverflow questions without raw answers - returns post IDs
     */
    @Query("SELECT rq.postId FROM RawQuestion rq " +
           "WHERE rq.sourcePlatform = 'stackoverflow' " +
           "AND rq.postId IS NOT NULL " +
           "AND NOT EXISTS (SELECT 1 FROM RawAnswer ra WHERE ra.rawQuestionId = rq.id) " +
           "ORDER BY rq.postId")
    List<Integer> findStackOverflowPostIdsWithoutAnswers();

    /**
     * Find StackOverflow questions without raw answers - returns full entities
     */
    @Query("SELECT rq FROM RawQuestion rq " +
           "WHERE rq.sourcePlatform = 'stackoverflow' " +
           "AND rq.postId IS NOT NULL " +
           "AND NOT EXISTS (SELECT 1 FROM RawAnswer ra WHERE ra.rawQuestionId = rq.id) " +
           "ORDER BY rq.postId")
    List<RawQuestion> findStackOverflowQuestionsWithoutAnswers();

    /**
     * Count StackOverflow questions without raw answers
     */
    @Query("SELECT COUNT(rq) FROM RawQuestion rq " +
           "WHERE rq.sourcePlatform = 'stackoverflow' " +
           "AND rq.postId IS NOT NULL " +
           "AND NOT EXISTS (SELECT 1 FROM RawAnswer ra WHERE ra.rawQuestionId = rq.id)")
    long countStackOverflowQuestionsWithoutAnswers();
} 