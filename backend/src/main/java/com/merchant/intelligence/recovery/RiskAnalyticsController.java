package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "http://localhost:5173")
public class RiskAnalyticsController {

    private final RiskAnalyticsService riskAnalyticsService;

    public RiskAnalyticsController(
            RiskAnalyticsService riskAnalyticsService) {

        this.riskAnalyticsService = riskAnalyticsService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getRiskAnalytics() {

        return ResponseEntity.ok(
                riskAnalyticsService.getRiskAnalytics()
        );
    }
}