package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "http://localhost:5173")
public class RiskSignalController {

    private final RiskSignalService riskSignalService;

    public RiskSignalController(
            RiskSignalService riskSignalService) {

        this.riskSignalService = riskSignalService;
    }

    @GetMapping("/signals/{transactionId}")
    public ResponseEntity<Map<String, Object>> getRiskSignals(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                riskSignalService.calculateRiskSignals(
                        transactionId
                )
        );
    }
}