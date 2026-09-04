package com.merchant.intelligence.api.controller;

import com.merchant.intelligence.api.dto.TransactionInput;
import com.merchant.intelligence.api.service.RevenueAnalyticsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class RevenueAnalyticsApiController {

    private final RevenueAnalyticsApiService revenueAnalyticsApiService;

    public RevenueAnalyticsApiController(
            RevenueAnalyticsApiService revenueAnalyticsApiService) {

        this.revenueAnalyticsApiService =
                revenueAnalyticsApiService;
    }

    @PostMapping("/revenue")
    public ResponseEntity<Map<String, Object>> analyzeRevenue(
            @RequestBody List<TransactionInput> transactions) {

        return ResponseEntity.ok(
                revenueAnalyticsApiService.analyze(transactions)
        );
    }
}