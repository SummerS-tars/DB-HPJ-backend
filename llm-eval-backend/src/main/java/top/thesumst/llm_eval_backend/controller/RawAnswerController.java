package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.RawAnswerResponse;
import top.thesumst.llm_eval_backend.service.RawAnswerService;

/**
 * Controller for raw answer operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Raw Answers", description = "原始答案管理API")
public class RawAnswerController {

    private final RawAnswerService rawAnswerService;

    @Operation(summary = "导入原始答案", description = "从CSV文件批量导入原始答案")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "导入成功",
                content = @Content(schema = @Schema(implementation = ImportResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/raw-answers/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<ImportResponse>> importAnswers(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "来源平台", example = "stackoverflow")
            @RequestParam(value = "sourcePlatform", required = false, defaultValue = "stackoverflow") String sourcePlatform) {
        
        log.info("Importing raw answers from file: {}, platform: {}", 
                file.getOriginalFilename(), sourcePlatform);
        
        ImportResponse result = rawAnswerService.importFromFile(file, sourcePlatform);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result, "原始答案导入完成"));
    }

    @Operation(summary = "查询原始答案列表", description = "分页查询原始答案列表，支持筛选和排序")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @GetMapping("/raw-answers")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<RawAnswerResponse>>> getAnswers(
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @Parameter(description = "来源平台筛选")
            @RequestParam(value = "sourcePlatform", required = false) String sourcePlatform) {
        
        Page<RawAnswerResponse> result = rawAnswerService.getRawAnswers(
                page, size, sortBy, order, sourcePlatform);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取问题的答案", description = "根据问题ID分页查询其原始答案列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "问题不存在")
    })
    @GetMapping("/raw-questions/{questionId}/answers")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<RawAnswerResponse>>> getAnswersByQuestionId(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Integer questionId,
            @Parameter(description = "页码", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(value = "order", defaultValue = "asc") String order) {
        
        Page<RawAnswerResponse> result = rawAnswerService.getRawAnswersByQuestionId(
                questionId, page, size, sortBy, order);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个原始答案", description = "根据ID获取原始答案详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "答案不存在")
    })
    @GetMapping("/raw-answers/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<RawAnswerResponse>> getAnswer(
            @Parameter(description = "答案ID", required = true)
            @PathVariable Integer id) {
        
        RawAnswerResponse result = rawAnswerService.getRawAnswerById(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }
} 