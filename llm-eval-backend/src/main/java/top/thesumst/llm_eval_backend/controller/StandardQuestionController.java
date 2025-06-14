package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.thesumst.llm_eval_backend.dto.request.StandardQuestionImportRequest;
import top.thesumst.llm_eval_backend.dto.request.TagAddRequest;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.StandardQuestionResponse;
import top.thesumst.llm_eval_backend.entity.enums.QuestionType;
import top.thesumst.llm_eval_backend.entity.enums.StandardQuestionStatus;
import top.thesumst.llm_eval_backend.service.StandardQuestionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * REST controller for standard question management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/std-questions")
@RequiredArgsConstructor
@Validated
@Tag(name = "标准问题管理", description = "标准问题管理相关接口")
public class StandardQuestionController {

    private final StandardQuestionService standardQuestionService;

    @Operation(summary = "批量导入标准问题", description = "批量导入标准问题，必须关联原始问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导入成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @PostMapping("/import")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<ImportResponse>> importStandardQuestions(
            @Valid @RequestBody List<StandardQuestionImportRequest> requests) {
        
        log.info("Importing {} standard questions", requests.size());
        
        ImportResponse result = standardQuestionService.importStandardQuestions(requests);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "查询标准问题列表", description = "分页查询标准问题，支持多种筛选条件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<StandardQuestionResponse>>> getStandardQuestions(
            @Parameter(description = "页码", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) int size,
            
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,
            
            @Parameter(description = "问题类型", required = true)
            @RequestParam QuestionType type,
            
            @Parameter(description = "问题状态")
            @RequestParam(required = false) StandardQuestionStatus status,
            
            @Parameter(description = "版本筛选")
            @RequestParam(required = false) String version,
            
            @Parameter(description = "标签筛选（逗号分隔）")
            @RequestParam(required = false) String tags,
            
            @Parameter(description = "原始问题ID筛选")
            @RequestParam(required = false) Long originalRawQuestionId) {
        
        log.info("Fetching standard questions with filters - type: {}, status: {}, version: {}, tags: {}", 
                type, status, version, tags);
        
        Page<StandardQuestionResponse> result = standardQuestionService.getStandardQuestions(
                page, size, sortBy, sortDirection, type, status, version, tags, originalRawQuestionId);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个标准问题", description = "根据ID获取标准问题详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "问题不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<StandardQuestionResponse>> getStandardQuestion(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Long id) {
        
        log.info("Fetching standard question: {}", id);
        
        StandardQuestionResponse result = standardQuestionService.getStandardQuestionById(id);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "添加标签到标准问题", description = "为指定标准问题添加标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "404", description = "问题不存在"),
        @ApiResponse(responseCode = "409", description = "标签已存在")
    })
    @PostMapping("/{id}/tags")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<StandardQuestionResponse>> addTag(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody TagAddRequest request) {
        
        log.info("Adding tag '{}' to standard question {}", request.getTagName(), id);
        
        StandardQuestionResponse result = standardQuestionService.addTagToQuestion(id, request);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "从标准问题移除标签", description = "从指定标准问题移除标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "移除成功"),
        @ApiResponse(responseCode = "404", description = "问题或标签不存在")
    })
    @DeleteMapping("/{id}/tags/{tagName}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<StandardQuestionResponse>> removeTag(
            @Parameter(description = "问题ID", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "标签名", required = true)
            @PathVariable String tagName) {
        
        log.info("Removing tag '{}' from standard question {}", tagName, id);
        
        StandardQuestionResponse result = standardQuestionService.removeTagFromQuestion(id, tagName);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "导出标准问题", description = "按版本、类型和可选标签导出标准问题为JSON文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "未找到符合条件的问题")
    })
    @GetMapping("/export")
    public ResponseEntity<String> exportStandardQuestions(
            @Parameter(description = "问题类型", required = true)
            @RequestParam QuestionType type,
            
            @Parameter(description = "版本", required = true)
            @RequestParam String version,
            
            @Parameter(description = "标签（可选）", required = false)
            @RequestParam(required = false) String tag) {
        
        log.info("Exporting standard questions - type: {}, version: {}, tag: {}", type, version, tag);
        
        String jsonContent = standardQuestionService.exportStandardQuestions(version, type, tag);
        String filename = standardQuestionService.generateExportFilename(version, type, tag);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonContent);
    }
}

/**
 * REST controller for raw question to standard question relationship
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/raw-questions")
@RequiredArgsConstructor
@Validated
@Tag(name = "原始问题-标准问题关联", description = "原始问题转换为标准问题的关联接口")
class RawQuestionStandardQuestionController {

    private final StandardQuestionService standardQuestionService;

    @Operation(summary = "获取原始问题的标准问题", description = "获取从某个原始问题转换的所有标准问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "原始问题不存在")
    })
    @GetMapping("/{rawQuestionId}/std-questions")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Page<StandardQuestionResponse>>> getStandardQuestionsByRawQuestion(
            @Parameter(description = "原始问题ID", required = true)
            @PathVariable Long rawQuestionId,
            
            @Parameter(description = "页码", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) int size,
            
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Fetching standard questions for raw question: {}", rawQuestionId);
        
        Page<StandardQuestionResponse> result = standardQuestionService.getStandardQuestionsByRawQuestionId(
                rawQuestionId, page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }
} 