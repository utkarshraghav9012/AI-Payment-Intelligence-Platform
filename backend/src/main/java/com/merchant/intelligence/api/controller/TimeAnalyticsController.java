package com.merchant.intelligence.api.controller;

import com.merchant.intelligence.api.dto.AnalyticsRequest;
import com.merchant.intelligence.api.dto.TransactionInput;
import com.merchant.intelligence.api.service.TimeAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class TimeAnalyticsController {

    private final TimeAnalyticsService timeAnalyticsService;

    public TimeAnalyticsController(
            TimeAnalyticsService timeAnalyticsService) {

        this.timeAnalyticsService = timeAnalyticsService;
    }

    @PostMapping("/time")
    public ResponseEntity<Map<String, Object>> analyzeTime(
            @RequestBody AnalyticsPayload payload) {

        if (payload == null
                || payload.getTransactions() == null
                || payload.getRequest() == null) {

            throw new IllegalArgumentException(
                    "request and transactions are required."
            );
        }

        return ResponseEntity.ok(
                timeAnalyticsService.analyze(
                        payload.getTransactions(),
                        payload.getRequest()
                )
        );
    }

    public static class AnalyticsPayload {

        private List<TransactionInput> transactions;
        private AnalyticsRequest request;

        public List<TransactionInput> getTransactions() {
            return transactions;
        }

        public void setTransactions(
                List<TransactionInput> transactions) {

            this.transactions = transactions;
        }

        public AnalyticsRequest getRequest() {
            return request;
        }

        public void setRequest(
                AnalyticsRequest request) {

            this.request = request;
        }
    }
}