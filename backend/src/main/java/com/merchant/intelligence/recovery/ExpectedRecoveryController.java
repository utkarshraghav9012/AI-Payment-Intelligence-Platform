package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpectedRecoveryController {

    private final ExpectedRecoveryService expectedRecoveryService;

    public ExpectedRecoveryController(
            ExpectedRecoveryService expectedRecoveryService) {

        this.expectedRecoveryService =
                expectedRecoveryService;
    }

    @GetMapping("/expected-recovery")
    public ResponseEntity<List<Map<String, Object>>>
    getExpectedRecoveryValues() {

        return ResponseEntity.ok(
                expectedRecoveryService
                        .getExpectedRecoveryValues()
        );
    }
}