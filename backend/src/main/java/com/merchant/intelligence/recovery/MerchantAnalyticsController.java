package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class MerchantAnalyticsController {

    private final MerchantAnalyticsService merchantAnalyticsService;

    public MerchantAnalyticsController(
            MerchantAnalyticsService merchantAnalyticsService) {

        this.merchantAnalyticsService = merchantAnalyticsService;
    }

    @GetMapping("/merchant-overview")
    public ResponseEntity<Map<String, Object>> getMerchantOverview() {

        return ResponseEntity.ok(
                merchantAnalyticsService.getMerchantOverview()
        );
    }
}