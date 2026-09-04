package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryDecisionController {

    private final RecoveryDecisionService recoveryDecisionService;

    public RecoveryDecisionController(
            RecoveryDecisionService recoveryDecisionService) {

        this.recoveryDecisionService =
                recoveryDecisionService;
    }

    @GetMapping("/decision/{transactionId}")
    public ResponseEntity<Map<String, Object>>
    getDecision(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                recoveryDecisionService
                        .getDecision(transactionId)
        );
    }
}