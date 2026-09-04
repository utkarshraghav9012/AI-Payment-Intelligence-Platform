package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentFailureAnalysisController {

    private final PaymentFailureAnalysisService paymentFailureAnalysisService;

    public PaymentFailureAnalysisController(
            PaymentFailureAnalysisService paymentFailureAnalysisService) {

        this.paymentFailureAnalysisService =
                paymentFailureAnalysisService;
    }

    @GetMapping("/payment-failure-analysis")
    public ResponseEntity<List<Map<String, Object>>>
    getPaymentFailureAnalysis() {

        return ResponseEntity.ok(
                paymentFailureAnalysisService
                        .getPaymentFailureAnalysis()
        );
    }
}