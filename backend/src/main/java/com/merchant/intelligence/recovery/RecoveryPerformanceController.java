package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryPerformanceController {

    private final RecoveryPerformanceService recoveryPerformanceService;

    public RecoveryPerformanceController(
            RecoveryPerformanceService recoveryPerformanceService) {

        this.recoveryPerformanceService = recoveryPerformanceService;
    }

    @GetMapping("/recovery-performance")
    public ResponseEntity<Map<String, Object>> getRecoveryPerformance() {

        return ResponseEntity.ok(
                recoveryPerformanceService.getRecoveryPerformance()
        );
    }
}