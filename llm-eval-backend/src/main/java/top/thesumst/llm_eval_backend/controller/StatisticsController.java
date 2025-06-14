package top.thesumst.llm_eval_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.thesumst.llm_eval_backend.dto.response.ApiResponse;
import top.thesumst.llm_eval_backend.dto.response.OverallStatisticsResponse;
import top.thesumst.llm_eval_backend.service.StatisticsService;

/**
 * Controller for system-wide statistics
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "System-wide statistics APIs")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Get overall system statistics
     */
    @GetMapping("/overall")
    @Operation(summary = "Get overall system statistics", 
               description = "Retrieve comprehensive system-wide statistics from all modules")
    public ResponseEntity<ApiResponse<OverallStatisticsResponse>> getOverallStatistics() {
        log.info("Getting overall system statistics");

        OverallStatisticsResponse response = statisticsService.getOverallStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 