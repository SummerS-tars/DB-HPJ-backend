package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.thesumst.llm_eval_backend.dto.request.AnalysisTagCreateRequest;
import top.thesumst.llm_eval_backend.dto.response.AnalysisTagResponse;
import top.thesumst.llm_eval_backend.dto.response.ApiResponse;
import top.thesumst.llm_eval_backend.service.AnalysisTagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Controller for Analysis Tag management
 */
@RestController
@RequestMapping("/api/v1/analysis-tags")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Analysis Tag Management", description = "APIs for managing analysis tags")
public class AnalysisTagController {

    private final AnalysisTagService analysisTagService;

    /**
     * Create a new analysis tag
     */
    @PostMapping
    @Operation(summary = "Create analysis tag", description = "Create a new analysis tag")
    public ResponseEntity<ApiResponse<AnalysisTagResponse>> createAnalysisTag(
            @Valid @RequestBody AnalysisTagCreateRequest request) {
        log.info("Creating analysis tag for evaluation tag ID: {}, model: {}", 
                request.getEvaluationTagId(), request.getModel());

        AnalysisTagResponse response = analysisTagService.createAnalysisTag(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis tag by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get analysis tag by ID", description = "Retrieve analysis tag by its ID")
    public ResponseEntity<ApiResponse<AnalysisTagResponse>> getAnalysisTagById(
            @Parameter(description = "Analysis tag ID") 
            @PathVariable @Positive Long id) {
        log.info("Getting analysis tag by ID: {}", id);

        AnalysisTagResponse response = analysisTagService.getAnalysisTagById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all analysis tags with pagination
     */
    @GetMapping
    @Operation(summary = "Get all analysis tags", description = "Retrieve all analysis tags with pagination")
    public ResponseEntity<ApiResponse<Page<AnalysisTagResponse>>> getAllAnalysisTags(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "analysisTagId") String sortBy,
            @Parameter(description = "Sort direction") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Getting all analysis tags - page: {}, size: {}", page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AnalysisTagResponse> response = analysisTagService.getAllAnalysisTags(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis tags by evaluation tag ID
     */
    @GetMapping("/by-evaluation-tag/{evaluationTagId}")
    @Operation(summary = "Get analysis tags by evaluation tag ID", 
               description = "Retrieve analysis tags for a specific evaluation tag")
    public ResponseEntity<ApiResponse<List<AnalysisTagResponse>>> getAnalysisTagsByEvaluationTagId(
            @Parameter(description = "Evaluation tag ID") 
            @PathVariable @Positive Long evaluationTagId) {
        log.info("Getting analysis tags by evaluation tag ID: {}", evaluationTagId);

        List<AnalysisTagResponse> response = analysisTagService.getAnalysisTagsByEvaluationTagId(evaluationTagId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis tags by model
     */
    @GetMapping("/by-model")
    @Operation(summary = "Get analysis tags by model", 
               description = "Retrieve analysis tags for a specific model")
    public ResponseEntity<ApiResponse<List<AnalysisTagResponse>>> getAnalysisTagsByModel(
            @Parameter(description = "Model name") 
            @RequestParam String model) {
        log.info("Getting analysis tags by model: {}", model);

        List<AnalysisTagResponse> response = analysisTagService.getAnalysisTagsByModel(model);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update analysis tag
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update analysis tag", description = "Update an existing analysis tag")
    public ResponseEntity<ApiResponse<AnalysisTagResponse>> updateAnalysisTag(
            @Parameter(description = "Analysis tag ID") 
            @PathVariable @Positive Long id,
            @Valid @RequestBody AnalysisTagCreateRequest request) {
        log.info("Updating analysis tag with ID: {}", id);

        AnalysisTagResponse response = analysisTagService.updateAnalysisTag(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete analysis tag
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete analysis tag", description = "Delete an analysis tag and its associated analysis results")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysisTag(
            @Parameter(description = "Analysis tag ID") 
            @PathVariable @Positive Long id) {
        log.info("Deleting analysis tag with ID: {}", id);

        analysisTagService.deleteAnalysisTag(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Analysis tag deleted successfully"));
    }
} 