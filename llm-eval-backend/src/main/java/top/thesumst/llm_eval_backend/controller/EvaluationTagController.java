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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.thesumst.llm_eval_backend.dto.request.EvaluationTagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationTagResponse;
import top.thesumst.llm_eval_backend.dto.response.EvaluationStatisticsResponse;
import top.thesumst.llm_eval_backend.service.EvaluationTagService;

import java.util.List;

/**
 * Controller for evaluation tag operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/evaluation-tags")
@RequiredArgsConstructor
@Tag(name = "Evaluation Tags", description = "评估标签管理API")
public class EvaluationTagController {

    private final EvaluationTagService evaluationTagService;

    @Operation(summary = "创建评估标签", description = "为模型和数据集版本创建新的评估标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "创建成功",
                content = @Content(schema = @Schema(implementation = EvaluationTagResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "评估标签已存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<EvaluationTagResponse>> createEvaluationTag(
            @Valid @RequestBody EvaluationTagCreateRequest request) {
        
        log.info("Creating evaluation tag for model: {}, version: {}", request.getModel(), request.getDataSetVersion());
        
        EvaluationTagResponse result = evaluationTagService.createEvaluationTag(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "评估标签创建成功"));
    }

    @Operation(summary = "查询评估标签列表", description = "分页查询评估标签列表，支持按模型和数据集版本筛选")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<EvaluationTagResponse>>> getEvaluationTags(
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "tagId")
            @RequestParam(value = "sortBy", defaultValue = "tagId") String sortBy,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Parameter(description = "模型名称筛选")
            @RequestParam(value = "model", required = false) String model,
            @Parameter(description = "数据集版本筛选")
            @RequestParam(value = "dataSetVersion", required = false) String dataSetVersion,
            @Parameter(description = "评估次数筛选")
            @RequestParam(value = "evaluationTime", required = false) Integer evaluationTime) {
        
        Page<EvaluationTagResponse> result = evaluationTagService.getEvaluationTags(
                page, size, sortBy, order, model, dataSetVersion, evaluationTime);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个评估标签", description = "根据ID获取评估标签详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "评估标签不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<EvaluationTagResponse>> getEvaluationTag(
            @Parameter(description = "评估标签ID", required = true)
            @PathVariable Long id) {
        
        EvaluationTagResponse result = evaluationTagService.getEvaluationTagById(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取每个模型的最新评估标签", description = "获取每个模型的最新评估标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/latest-by-model")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<EvaluationTagResponse>>> getLatestEvaluationsByModel(
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        Page<EvaluationTagResponse> result = evaluationTagService.getLatestEvaluationsByModel(page, size);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取所有模型列表", description = "获取所有已注册的模型名称列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/models")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<List<String>>> getAllModels() {
        
        List<String> result = evaluationTagService.getAllUniqueModels();
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取所有数据集版本列表", description = "获取所有已使用的数据集版本列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/versions")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<List<String>>> getAllDataSetVersions() {
        
        List<String> result = evaluationTagService.getAllUniqueDataSetVersions();
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取评估标签统计信息", description = "获取指定评估标签的详细统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "评估标签不存在")
    })
    @GetMapping("/{id}/statistics")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<EvaluationStatisticsResponse>> getEvaluationStatistics(
            @Parameter(description = "评估标签ID", required = true)
            @PathVariable Long id) {
        
        EvaluationStatisticsResponse result = evaluationTagService.getEvaluationStatistics(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "删除评估标签", description = "删除指定的评估标签（如果没有关联的评估结果）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "评估标签不存在"),
        @ApiResponse(responseCode = "409", description = "存在关联的评估结果，无法删除")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Void>> deleteEvaluationTag(
            @Parameter(description = "评估标签ID", required = true)
            @PathVariable Long id) {
        
        log.info("Deleting evaluation tag: id={}", id);
        
        evaluationTagService.deleteEvaluationTag(id);
        
        return ResponseEntity.noContent().build();
    }
} 