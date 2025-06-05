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
import top.thesumst.llm_eval_backend.dto.request.VersionCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.VersionResponse;
import top.thesumst.llm_eval_backend.service.VersionService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for version management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/versions")
@RequiredArgsConstructor
@Validated
@Tag(name = "版本管理", description = "数据集版本管理相关接口")
public class VersionController {

    private final VersionService versionService;

    @Operation(summary = "创建版本", description = "创建新的数据集版本")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "版本已存在")
    })
    @PostMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<VersionResponse>> createVersion(
            @Valid @RequestBody VersionCreateRequest request) {
        
        log.info("Creating version: {}", request.getVersion());
        
        VersionResponse result = versionService.createVersion(request);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取版本列表", description = "获取所有数据集版本")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<List<VersionResponse>>> getAllVersions() {
        
        log.info("Fetching all versions");
        
        List<VersionResponse> result = versionService.getAllVersions();
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "获取单个版本", description = "根据版本名获取版本详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "版本不存在")
    })
    @GetMapping("/{version}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<VersionResponse>> getVersion(
            @Parameter(description = "版本名", required = true)
            @PathVariable String version) {
        
        log.info("Fetching version: {}", version);
        
        VersionResponse result = versionService.getVersionByName(version);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(result));
    }

    @Operation(summary = "删除版本", description = "删除指定版本（仅在无关联标准问题时可删除）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "版本不存在"),
        @ApiResponse(responseCode = "400", description = "版本仍有关联数据，无法删除")
    })
    @DeleteMapping("/{version}")
    public ResponseEntity<top.thesumst.llm_eval_backend.dto.response.ApiResponse<Void>> deleteVersion(
            @Parameter(description = "版本名", required = true)
            @PathVariable String version) {
        
        log.info("Deleting version: {}", version);
        
        versionService.deleteVersion(version);
        
        return ResponseEntity.ok(top.thesumst.llm_eval_backend.dto.response.ApiResponse.success(null, "版本删除成功"));
    }
} 