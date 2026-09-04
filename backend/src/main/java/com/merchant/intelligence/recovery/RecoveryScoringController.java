package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryScoringController {

    private final RecoveryScoringService recoveryScoringService;

    public RecoveryScoringController(
            RecoveryScoringService recoveryScoringService) {

        this.recoveryScoringService = recoveryScoringService;
    }

    @GetMapping("/score/{transactionId}")
    public ResponseEntity<Map<String, Object>> getRecoveryScore(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                recoveryScoringService.calculateRecoveryScore(
                        transactionId
                )
        );
    }
}