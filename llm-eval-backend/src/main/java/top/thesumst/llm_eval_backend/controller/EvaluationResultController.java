package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.thesumst.llm_eval_backend.dto.request.EvaluationResultImportRequest;
import top.thesumst.llm_eval_backend.dto.request.EvaluationResultStatusUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationResultResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.entity.enums.EvaluationResultStatus;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.service.EvaluationResultService;

import java.util.List;

/**
 * Controller for evaluation result operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/evaluation-results")
@RequiredArgsConstructor
@Tag(name = "Evaluation Results", description = "评估结果管理API")
public class EvaluationResultController {

    private final EvaluationResultService evaluationResultService;

    @Operation(summary = "导入评估结果", description = "从CSV文件批量导入评估结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "导入成功",
                content = @Content(schema = @Schema(implementation = ImportResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "评估标签不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<ImportResponse>> importEvaluationResults(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "评估标签ID", required = true)
            @RequestParam("evaluationTagId") Long evaluationTagId) {
        
        log.info("Importing evaluation results from file: {}, tag: {}", 
                file.getOriginalFilename(), evaluationTagId);
        
        ImportResponse result = evaluationResultService.importFromFile(file, evaluationTagId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "评估结果导入完成"));
    }

    @Operation(summary = "批量导入评估结果", description = "通过JSON数据批量导入评估结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "导入成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/batch-import")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<ImportResponse>> batchImportEvaluationResults(
            @Valid @RequestBody List<EvaluationResultImportRequest> requests) {
        
        log.info("Batch importing {} evaluation results", requests.size());
        
        ImportResponse result = evaluationResultService.batchImport(requests);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "评估结果批量导入完成"));
    }

    @Operation(summary = "查询评估结果列表", description = "分页查询评估结果列表，支持按评估标签、状态、类型筛选")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<EvaluationResultResponse>>> getEvaluationResults(
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Parameter(description = "评估标签ID筛选")
            @RequestParam(value = "evaluationTagId", required = false) Long evaluationTagId,
            @Parameter(description = "状态筛选")
            @RequestParam(value = "status", required = false) EvaluationResultStatus status,
            @Parameter(description = "问题类型筛选")
            @RequestParam(value = "type", required = false) QuestionType type) {
        
        Page<EvaluationResultResponse> result = evaluationResultService.getEvaluationResults(
                page, size, sortBy, order, evaluationTagId, status, type);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个评估结果", description = "根据ID获取评估结果详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "评估结果不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<EvaluationResultResponse>> getEvaluationResult(
            @Parameter(description = "评估结果ID", required = true)
            @PathVariable Long id) {
        
        EvaluationResultResponse result = evaluationResultService.getEvaluationResultById(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "更新评估结果状态", description = "更新评估结果的状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "评估结果不存在"),
        @ApiResponse(responseCode = "400", description = "状态无效")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<EvaluationResultResponse>> updateStatus(
            @Parameter(description = "评估结果ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EvaluationResultStatusUpdateRequest request) {
        
        log.info("Updating evaluation result status: id={}, status={}", id, request.getStatus());
        
        EvaluationResultResponse result = evaluationResultService.updateStatus(id, request);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "状态更新成功"));
    }

    @Operation(summary = "导出评估结果", description = "导出指定评估标签的所有评估结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "404", description = "评估标签不存在"),
        @ApiResponse(responseCode = "400", description = "导出格式不支持")
    })
    @GetMapping("/export")
    public ResponseEntity<String> exportEvaluationResults(
            @Parameter(description = "评估标签ID", required = true)
            @RequestParam("evaluationTagId") Long evaluationTagId,
            @Parameter(description = "导出格式", example = "csv")
            @RequestParam(value = "format", defaultValue = "csv") String format) {
        
        log.info("Exporting evaluation results for tag: {}, format: {}", evaluationTagId, format);
        
        String result = evaluationResultService.exportEvaluationResults(evaluationTagId, format);
        
        String contentType = "csv".equalsIgnoreCase(format) ? "text/csv" : "application/json";
        String filename = "evaluation_results_" + evaluationTagId + "." + format.toLowerCase();
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", contentType + "; charset=UTF-8")
                .body(result);
    }
} 