package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.thesumst.llm_eval_backend.dto.request.TagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.TagResponse;
import top.thesumst.llm_eval_backend.service.TagService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for tag management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Validated
@Tag(name = "标签管理", description = "标准问题标签管理相关接口")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "创建标签", description = "创建新的问题标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "标签已存在")
    })
    @PostMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<TagResponse>> createTag(
            @Valid @RequestBody TagCreateRequest request) {
        
        log.info("Creating tag: {}", request.getTag());
        
        TagResponse result = tagService.createTag(request);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取标签列表", description = "获取所有问题标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<List<TagResponse>>> getAllTags(
            @Parameter(description = "搜索关键词", required = false)
            @RequestParam(required = false) String query) {
        
        log.info("Fetching tags with query: {}", query);
        
        List<TagResponse> result;
        if (query != null && !query.trim().isEmpty()) {
            result = tagService.searchTags(query);
        } else {
            result = tagService.getAllTags();
        }
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个标签", description = "根据标签名获取标签详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @GetMapping("/{tag}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<TagResponse>> getTag(
            @Parameter(description = "标签名", required = true)
            @PathVariable String tag) {
        
        log.info("Fetching tag: {}", tag);
        
        TagResponse result = tagService.getTagByName(tag);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "删除标签", description = "删除指定标签（仅在无关联标准问题时可删除）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "标签不存在"),
        @ApiResponse(responseCode = "400", description = "标签仍有关联数据，无法删除")
    })
    @DeleteMapping("/{tag}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Void>> deleteTag(
            @Parameter(description = "标签名", required = true)
            @PathVariable String tag) {
        
        log.info("Deleting tag: {}", tag);
        
        tagService.deleteTag(tag);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(null, "标签删除成功"));
    }
} 