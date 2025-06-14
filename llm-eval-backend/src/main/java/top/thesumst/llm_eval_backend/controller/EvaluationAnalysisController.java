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
import top.thesumst.llm_eval_backend.dto.request.EvaluationAnalysisImportRequest;
import top.thesumst.llm_eval_backend.dto.response.EvaluationAnalysisResponse;
import top.thesumst.llm_eval_backend.dto.response.AnalysisStatisticsResponse;
import top.thesumst.llm_eval_backend.dto.response.ImportResponse;
import top.thesumst.llm_eval_backend.dto.response.ApiResponse;
import top.thesumst.llm_eval_backend.service.EvaluationAnalysisService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller for Evaluation Analysis management
 */
@RestController
@RequestMapping("/api/evaluation-analysis")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Evaluation Analysis Management", description = "APIs for managing evaluation analysis results")
public class EvaluationAnalysisController {

    private final EvaluationAnalysisService evaluationAnalysisService;

    /**
     * Import evaluation analysis results
     */
    @PostMapping("/import")
    @Operation(summary = "Import analysis results", description = "Import evaluation analysis results in batch")
    public ResponseEntity<ApiResponse<ImportResponse>> importAnalysisResults(
            @Valid @RequestBody EvaluationAnalysisImportRequest request) {
        log.info("Importing analysis results for analysis tag ID: {}, count: {}", 
                request.getAnalysisTagId(), request.getResults().size());

        ImportResponse response = evaluationAnalysisService.importAnalysisResults(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis result by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get analysis result by ID", description = "Retrieve analysis result by its ID")
    public ResponseEntity<ApiResponse<EvaluationAnalysisResponse>> getAnalysisResultById(
            @Parameter(description = "Analysis result ID") 
            @PathVariable @Positive Long id) {
        log.info("Getting analysis result by ID: {}", id);

        EvaluationAnalysisResponse response = evaluationAnalysisService.getAnalysisResultById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all analysis results with pagination
     */
    @GetMapping
    @Operation(summary = "Get all analysis results", description = "Retrieve all analysis results with pagination")
    public ResponseEntity<ApiResponse<Page<EvaluationAnalysisResponse>>> getAllAnalysisResults(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Getting all analysis results - page: {}, size: {}", page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EvaluationAnalysisResponse> response = evaluationAnalysisService.getAllAnalysisResults(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis results by analysis tag ID
     */
    @GetMapping("/by-tag/{analysisTagId}")
    @Operation(summary = "Get analysis results by analysis tag ID", 
               description = "Retrieve analysis results for a specific analysis tag")
    public ResponseEntity<ApiResponse<Page<EvaluationAnalysisResponse>>> getAnalysisResultsByTagId(
            @Parameter(description = "Analysis tag ID") 
            @PathVariable @Positive Long analysisTagId,
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Getting analysis results by analysis tag ID: {}", analysisTagId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EvaluationAnalysisResponse> response = evaluationAnalysisService.getAnalysisResultsByTagId(analysisTagId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get overall analysis statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get analysis statistics", description = "Retrieve overall analysis statistics")
    public ResponseEntity<ApiResponse<AnalysisStatisticsResponse>> getAnalysisStatistics() {
        log.info("Getting overall analysis statistics");

        AnalysisStatisticsResponse response = evaluationAnalysisService.getAnalysisStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get analysis statistics by analysis tag ID
     */
    @GetMapping("/statistics/by-tag/{analysisTagId}")
    @Operation(summary = "Get analysis statistics by tag", 
               description = "Retrieve analysis statistics for a specific analysis tag")
    public ResponseEntity<ApiResponse<AnalysisStatisticsResponse>> getAnalysisStatisticsByTagId(
            @Parameter(description = "Analysis tag ID") 
            @PathVariable @Positive Long analysisTagId) {
        log.info("Getting analysis statistics by analysis tag ID: {}", analysisTagId);

        AnalysisStatisticsResponse response = evaluationAnalysisService.getAnalysisStatisticsByTagId(analysisTagId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete analysis result
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete analysis result", description = "Delete an analysis result")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysisResult(
            @Parameter(description = "Analysis result ID") 
            @PathVariable @Positive Long id) {
        log.info("Deleting analysis result with ID: {}", id);

        evaluationAnalysisService.deleteAnalysisResult(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Analysis result deleted successfully"));
    }
} 