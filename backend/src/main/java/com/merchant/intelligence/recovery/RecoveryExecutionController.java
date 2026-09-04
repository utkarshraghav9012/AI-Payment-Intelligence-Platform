package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryExecutionController {

    private final RecoveryExecutionService recoveryExecutionService;

    public RecoveryExecutionController(
            RecoveryExecutionService recoveryExecutionService) {

        this.recoveryExecutionService = recoveryExecutionService;
    }

    @PostMapping("/execute/{transactionId}")
    public ResponseEntity<Map<String, Object>> executeRecovery(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                recoveryExecutionService.executeRecovery(
                        transactionId
                )
        );
    }
}