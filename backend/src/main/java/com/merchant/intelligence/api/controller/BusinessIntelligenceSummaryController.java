package com.merchant.intelligence.api.controller;

import com.merchant.intelligence.api.dto.TransactionInput;
import com.merchant.intelligence.api.service.BusinessIntelligenceSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class BusinessIntelligenceSummaryController {

    private final BusinessIntelligenceSummaryService service;

    public BusinessIntelligenceSummaryController(
            BusinessIntelligenceSummaryService service) {

        this.service = service;
    }

    @PostMapping("/business-insight")
    public ResponseEntity<Map<String, Object>> analyze(
            @RequestBody List<TransactionInput> transactions) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions are required."
            );
        }

        return ResponseEntity.ok(
                service.analyze(transactions)
        );
    }
}