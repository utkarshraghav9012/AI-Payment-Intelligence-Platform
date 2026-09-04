package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class MerchantSegmentationController {

    private final MerchantSegmentationService merchantSegmentationService;

    public MerchantSegmentationController(
            MerchantSegmentationService merchantSegmentationService) {

        this.merchantSegmentationService =
                merchantSegmentationService;
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<Map<String, Object>>>
    getMerchantSegments() {

        return ResponseEntity.ok(
                merchantSegmentationService
                        .getMerchantSegments()
        );
    }
}