package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.thesumst.llm_eval_backend.dto.request.StandardAnswerCreateRequest;
import top.thesumst.llm_eval_backend.dto.request.StandardAnswerUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.ApiResponse;
import top.thesumst.llm_eval_backend.dto.response.StandardAnswerResponse;
import top.thesumst.llm_eval_backend.dto.response.StandardAnswerStatisticsResponse;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardAnswerStatus;
import top.thesumst.llm_eval_backend.service.StandardAnswerService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for standard answer management
 */
@RestController
@RequestMapping("/api/v1/std-answers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Standard Answers", description = "标准答案管理接口")
public class StandardAnswerController {

    private final StandardAnswerService standardAnswerService;

    /**
     * Create standard answer from candidate answer
     */
    @PostMapping("/from-candidate")
    @Operation(summary = "从候选答案创建标准答案", description = "从已接受的候选答案创建标准答案")
    public ResponseEntity<ApiResponse<StandardAnswerResponse>> createFromCandidateAnswer(
            @Parameter(description = "标准答案创建请求", required = true)
            @Valid @RequestBody StandardAnswerCreateRequest request) {
        
        log.info("Creating standard answer from candidate answer: {}", request.getCandidateAnswerId());
        
        StandardAnswerResponse result = standardAnswerService.createFromCandidateAnswer(request);
        
        return ResponseEntity.ok(ApiResponse.success(result, "标准答案创建成功"));
    }

    /**
     * Create standard answer from candidate answer by ID (simplified endpoint)
     */
    @PostMapping("/from-candidate/{candidateId}")
    @Operation(summary = "从候选答案ID创建标准答案", description = "通过候选答案ID快速创建标准答案")
    public ResponseEntity<ApiResponse<StandardAnswerResponse>> createFromCandidateAnswerId(
            @Parameter(description = "候选答案ID", required = true)
            @PathVariable Long candidateId,
            @Parameter(description = "答案分数 (0-10)", example = "8")
            @RequestParam(value = "score", required = false) Integer score) {
        
        log.info("Creating standard answer from candidate answer ID: {}, score: {}", candidateId, score);
        
        StandardAnswerResponse result = standardAnswerService.createFromCandidateAnswerId(candidateId, score);
        
        return ResponseEntity.ok(ApiResponse.success(result, "标准答案创建成功"));
    }

    /**
     * Get standard answers with filtering and pagination
     */
    @GetMapping
    @Operation(summary = "获取标准答案列表", description = "分页查询标准答案，支持多种过滤条件")
    public ResponseEntity<ApiResponse<Page<StandardAnswerResponse>>> getStandardAnswers(
            @Parameter(description = "标准问题ID")
            @RequestParam(value = "stdQuestionId", required = false) Long stdQuestionId,
            @Parameter(description = "问题类型")
            @RequestParam(value = "type", required = false) QuestionType type,
            @Parameter(description = "答案状态")
            @RequestParam(value = "status", required = false) StandardAnswerStatus status,
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        
        log.info("Getting standard answers with filters - stdQuestionId: {}, type: {}, status: {}, page: {}, size: {}", 
                stdQuestionId, type, status, page, size);
        
        Page<StandardAnswerResponse> result = standardAnswerService.getStandardAnswers(
                stdQuestionId, type, status, page, size, sort, direction);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get standard answer by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取标准答案详情", description = "根据ID获取标准答案详细信息")
    public ResponseEntity<ApiResponse<StandardAnswerResponse>> getStandardAnswerById(
            @Parameter(description = "标准答案ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting standard answer by id: {}", id);
        
        StandardAnswerResponse result = standardAnswerService.getStandardAnswerById(id);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Update standard answer
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新标准答案", description = "更新标准答案的状态和分数")
    public ResponseEntity<ApiResponse<StandardAnswerResponse>> updateStandardAnswer(
            @Parameter(description = "标准答案ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "标准答案更新请求", required = true)
            @Valid @RequestBody StandardAnswerUpdateRequest request) {
        
        log.info("Updating standard answer {} - status: {}, score: {}", id, request.getStatus(), request.getScore());
        
        StandardAnswerResponse result = standardAnswerService.updateStandardAnswer(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(result, "标准答案更新成功"));
    }

    /**
     * Update standard answer (partial update)
     */
    @PatchMapping("/{id}")
    @Operation(summary = "部分更新标准答案", description = "部分更新标准答案信息")
    public ResponseEntity<ApiResponse<StandardAnswerResponse>> patchStandardAnswer(
            @Parameter(description = "标准答案ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "标准答案更新请求", required = true)
            @Valid @RequestBody StandardAnswerUpdateRequest request) {
        
        log.info("Patching standard answer {} - status: {}, score: {}", id, request.getStatus(), request.getScore());
        
        StandardAnswerResponse result = standardAnswerService.updateStandardAnswer(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(result, "标准答案更新成功"));
    }

    /**
     * Delete standard answer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除标准答案", description = "根据ID删除标准答案")
    public ResponseEntity<ApiResponse<Void>> deleteStandardAnswer(
            @Parameter(description = "标准答案ID", required = true)
            @PathVariable Long id) {
        
        log.info("Deleting standard answer: {}", id);
        
        standardAnswerService.deleteStandardAnswer(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "标准答案删除成功"));
    }

    /**
     * Get high score answers
     */
    @GetMapping("/high-score")
    @Operation(summary = "获取高分答案", description = "获取分数达到指定阈值的标准答案")
    public ResponseEntity<ApiResponse<List<StandardAnswerResponse>>> getHighScoreAnswers(
            @Parameter(description = "分数阈值", example = "8", required = true)
            @RequestParam("threshold") Integer threshold,
            @Parameter(description = "问题类型", required = true)
            @RequestParam("type") QuestionType type) {
        
        log.info("Getting high score answers - threshold: {}, type: {}", threshold, type);
        
        List<StandardAnswerResponse> result = standardAnswerService.getHighScoreAnswers(threshold, type);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get standard answers by score range
     */
    @GetMapping("/score-range")
    @Operation(summary = "按分数范围查询标准答案", description = "查询指定分数范围内的标准答案")
    public ResponseEntity<ApiResponse<Page<StandardAnswerResponse>>> getStandardAnswersByScoreRange(
            @Parameter(description = "最低分数", example = "6", required = true)
            @RequestParam("minScore") Integer minScore,
            @Parameter(description = "最高分数", example = "10", required = true)
            @RequestParam("maxScore") Integer maxScore,
            @Parameter(description = "问题类型", required = true)
            @RequestParam("type") QuestionType type,
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "score")
            @RequestParam(value = "sort", defaultValue = "score") String sort,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        
        log.info("Getting standard answers by score range - minScore: {}, maxScore: {}, type: {}", 
                minScore, maxScore, type);
        
        Page<StandardAnswerResponse> result = standardAnswerService.getStandardAnswersByScoreRange(
                minScore, maxScore, type, page, size, sort, direction);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get standard answer statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取标准答案统计", description = "获取标准答案的各种统计信息")
    public ResponseEntity<ApiResponse<StandardAnswerStatisticsResponse>> getStatistics() {
        
        log.info("Getting standard answer statistics");
        
        StandardAnswerStatisticsResponse result = standardAnswerService.getStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}

/**
 * Extension to StandardQuestionController for standard answers
 */
@RestController
@RequestMapping("/api/v1/std-questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Standard Questions - Standard Answers", description = "标准问题的标准答案管理")
class StandardQuestionStandardAnswerController {

    private final StandardAnswerService standardAnswerService;

    /**
     * Get standard answers for a specific standard question
     */
    @GetMapping("/{id}/std-answers")
    @Operation(summary = "获取标准问题的标准答案", description = "获取指定标准问题的所有标准答案")
    public ResponseEntity<ApiResponse<List<StandardAnswerResponse>>> getStandardAnswersByStdQuestionId(
            @Parameter(description = "标准问题ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting standard answers for standard question: {}", id);
        
        List<StandardAnswerResponse> result = standardAnswerService.getStandardAnswersByStdQuestionId(id);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Find standard questions without standard answers
     */
    @GetMapping("/without-answers")
    @Operation(summary = "查找没有标准答案的标准问题", description = "查找指定类型中没有标准答案的标准问题ID列表")
    public ResponseEntity<ApiResponse<List<Long>>> findStandardQuestionIdsWithoutAnswers(
            @Parameter(description = "问题类型", required = true)
            @RequestParam("type") QuestionType type) {
        
        log.info("Finding standard questions without answers for type: {}", type);
        
        List<Long> result = standardAnswerService.findStandardQuestionIdsWithoutAnswers(type);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
} 