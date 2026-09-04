package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "http://localhost:5173")
public class RecoveryOpportunityController {

    private final RecoveryOpportunityService recoveryOpportunityService;

    public RecoveryOpportunityController(
            RecoveryOpportunityService recoveryOpportunityService) {

        this.recoveryOpportunityService =
                recoveryOpportunityService;
    }

    @GetMapping("/opportunity")
    public ResponseEntity<Map<String, Object>>
    getRecoveryOpportunity() {

        return ResponseEntity.ok(
                recoveryOpportunityService
                        .getRecoveryOpportunity()
        );
    }
}