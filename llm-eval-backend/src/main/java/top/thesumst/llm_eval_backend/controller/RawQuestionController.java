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
import top.thesumst.llm_eval_backend.dto.request.StatusUpdateRequest;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.RawQuestionResponse;
import top.thesumst.llm_eval_backend.entity.enums.RawQuestionStatus;
import top.thesumst.llm_eval_backend.service.RawQuestionService;

/**
 * Controller for raw question operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/raw-questions")
@RequiredArgsConstructor
@Tag(name = "Raw Questions", description = "原始问题管理API")
public class RawQuestionController {

    private final RawQuestionService rawQuestionService;

    @Operation(summary = "导入原始问题", description = "从CSV文件批量导入原始问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "导入成功",
                content = @Content(schema = @Schema(implementation = ImportResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<ImportResponse>> importQuestions(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "来源平台", example = "stackoverflow")
            @RequestParam(value = "sourcePlatform", required = false, defaultValue = "stackoverflow") String sourcePlatform) {
        
        log.info("Importing raw questions from file: {}, platform: {}", 
                file.getOriginalFilename(), sourcePlatform);
        
        ImportResponse result = rawQuestionService.importFromFile(file, sourcePlatform);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "原始问题导入完成"));
    }

    @Operation(summary = "查询原始问题列表", description = "分页查询原始问题列表，支持筛选和排序")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<RawQuestionResponse>>> getQuestions(
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @Parameter(description = "状态筛选")
            @RequestParam(value = "status", required = false) RawQuestionStatus status,
            @Parameter(description = "来源平台筛选")
            @RequestParam(value = "sourcePlatform", required = false) String sourcePlatform) {
        
        Page<RawQuestionResponse> result = rawQuestionService.getRawQuestions(
                page, size, sortBy, order, status, sourcePlatform);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个原始问题", description = "根据ID获取原始问题详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "问题不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<RawQuestionResponse>> getQuestion(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Integer id) {
        
        RawQuestionResponse result = rawQuestionService.getRawQuestionById(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "更新问题状态", description = "更新原始问题的状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "问题不存在"),
        @ApiResponse(responseCode = "400", description = "状态无效")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<RawQuestionResponse>> updateStatus(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody StatusUpdateRequest request) {
        
        log.info("Updating raw question status: id={}, status={}", id, request.getStatus());
        
        RawQuestionResponse result = rawQuestionService.updateStatus(id, request.getStatus());
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "状态更新成功"));
    }
} 