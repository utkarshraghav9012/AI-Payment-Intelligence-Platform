package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class FailureAnalyticsController {

    private final FailureAnalyticsService failureAnalyticsService;

    public FailureAnalyticsController(
            FailureAnalyticsService failureAnalyticsService) {

        this.failureAnalyticsService = failureAnalyticsService;
    }

    @GetMapping("/failures")
    public ResponseEntity<List<Map<String, Object>>> getFailureAnalytics() {

        return ResponseEntity.ok(
                failureAnalyticsService.getFailureAnalytics()
        );
    }
}