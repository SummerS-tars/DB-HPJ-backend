package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.thesumst.llm_eval_backend.dto.request.CandidateAnswerStatusUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.ApiResponse;
import top.thesumst.llm_eval_backend.dto.response.CandidateAnswerResponse;
import top.thesumst.llm_eval_backend.dto.response.CandidateAnswerStatisticsResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.entity.enums.CandidateAnswerStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.service.CandidateAnswerService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for candidate answer management
 */
@RestController
@RequestMapping("/api/v1/candidate-answers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Candidate Answers", description = "候选答案管理接口")
public class CandidateAnswerController {

    private final CandidateAnswerService candidateAnswerService;

    /**
     * Import candidate answers from CSV file
     */
    @PostMapping("/import")
    @Operation(summary = "导入候选答案", description = "从CSV文件导入候选答案")
    public ResponseEntity<ApiResponse<ImportResponse>> importCandidateAnswers(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "问题类型", required = true)
            @RequestParam("type") QuestionType type) {
        
        log.info("Importing candidate answers from file: {}, type: {}", 
                file.getOriginalFilename(), type);
        
        ImportResponse result = candidateAnswerService.importCandidateAnswers(file, type);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get candidate answers with filtering and pagination
     */
    @GetMapping
    @Operation(summary = "获取候选答案列表", description = "分页查询候选答案，支持多种过滤条件")
    public ResponseEntity<ApiResponse<Page<CandidateAnswerResponse>>> getCandidateAnswers(
            @Parameter(description = "标准问题ID")
            @RequestParam(value = "stdQuestionId", required = false) Long stdQuestionId,
            @Parameter(description = "问题类型")
            @RequestParam(value = "type", required = false) QuestionType type,
            @Parameter(description = "答案状态")
            @RequestParam(value = "status", required = false) CandidateAnswerStatus status,
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        
        log.info("Getting candidate answers with filters - stdQuestionId: {}, type: {}, status: {}, page: {}, size: {}", 
                stdQuestionId, type, status, page, size);
        
        Page<CandidateAnswerResponse> result = candidateAnswerService.getCandidateAnswers(
                stdQuestionId, type, status, page, size, sort, direction);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get candidate answer by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取候选答案详情", description = "根据ID获取候选答案详细信息")
    public ResponseEntity<ApiResponse<CandidateAnswerResponse>> getCandidateAnswerById(
            @Parameter(description = "候选答案ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting candidate answer by id: {}", id);
        
        CandidateAnswerResponse result = candidateAnswerService.getCandidateAnswerById(id);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Update candidate answer status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新候选答案状态", description = "更新候选答案的状态（PENDING/ACCEPTED/REJECTED）")
    public ResponseEntity<ApiResponse<CandidateAnswerResponse>> updateStatus(
            @Parameter(description = "候选答案ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "状态更新请求", required = true)
            @Valid @RequestBody CandidateAnswerStatusUpdateRequest request) {
        
        log.info("Updating candidate answer {} status to {}", id, request.getStatus());
        
        CandidateAnswerResponse result = candidateAnswerService.updateStatus(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get candidate answer statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取候选答案统计", description = "获取候选答案的各种统计信息")
    public ResponseEntity<ApiResponse<CandidateAnswerStatisticsResponse>> getStatistics() {
        
        log.info("Getting candidate answer statistics");
        
        CandidateAnswerStatisticsResponse result = candidateAnswerService.getStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Delete candidate answer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除候选答案", description = "根据ID删除候选答案")
    public ResponseEntity<ApiResponse<Void>> deleteCandidateAnswer(
            @Parameter(description = "候选答案ID", required = true)
            @PathVariable Long id) {
        
        log.info("Deleting candidate answer: {}", id);
        
        candidateAnswerService.deleteCandidateAnswer(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "候选答案删除成功"));
    }
}

/**
 * Extension to StandardQuestionController for candidate answers
 */
@RestController
@RequestMapping("/api/v1/std-questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Standard Questions - Candidate Answers", description = "标准问题的候选答案管理")
class StandardQuestionCandidateAnswerController {

    private final CandidateAnswerService candidateAnswerService;

    /**
     * Get candidate answers for a specific standard question
     */
    @GetMapping("/{id}/candidate-answers")
    @Operation(summary = "获取标准问题的候选答案", description = "获取指定标准问题的所有候选答案")
    public ResponseEntity<ApiResponse<List<CandidateAnswerResponse>>> getCandidateAnswersByStdQuestionId(
            @Parameter(description = "标准问题ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting candidate answers for standard question: {}", id);
        
        List<CandidateAnswerResponse> result = candidateAnswerService.getCandidateAnswersByStdQuestionId(id);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
} 