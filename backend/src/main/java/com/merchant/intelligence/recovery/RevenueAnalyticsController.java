package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class RevenueAnalyticsController {

    private final RevenueAnalyticsService analyticsService;

    public RevenueAnalyticsController(
            RevenueAnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {

        return ResponseEntity.ok(
                analyticsService.getRevenueAnalytics()
        );
    }
}