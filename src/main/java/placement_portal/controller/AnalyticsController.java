package placement_portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import placement_portal.service.AnalyticsService;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOverview() {

        return ResponseEntity.ok(
                analyticsService.getPlacementStatistics()
        );
    }

    @GetMapping("/applications")
    public ResponseEntity<Map<String, Long>> getApplicationStatistics() {

        return ResponseEntity.ok(
                analyticsService.getApplicationStatistics()
        );
    }

    @GetMapping("/placement")
    public ResponseEntity<Map<String, Object>> getPlacementStatistics() {

        return ResponseEntity.ok(
                analyticsService.getPlacementStatistics()
        );
    }
}