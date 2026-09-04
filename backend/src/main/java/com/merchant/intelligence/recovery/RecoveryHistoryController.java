package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryHistoryController {

    private final RecoveryAttemptRepository recoveryAttemptRepository;

    public RecoveryHistoryController(
            RecoveryAttemptRepository recoveryAttemptRepository) {

        this.recoveryAttemptRepository = recoveryAttemptRepository;
    }

    @GetMapping("/history/{transactionId}")
    public ResponseEntity<List<RecoveryAttempt>> getRecoveryHistory(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                recoveryAttemptRepository
                        .findByTransactionId(transactionId)
        );
    }
}