package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryController {

    private final RecoveryDecisionService recoveryDecisionService;

    public RecoveryController(
            RecoveryDecisionService recoveryDecisionService) {

        this.recoveryDecisionService =
                recoveryDecisionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> getRecommendation(
            @PathVariable String transactionId) {

        try {

            return ResponseEntity.ok(
                    recoveryDecisionService.getDecision(
                            transactionId
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }
}