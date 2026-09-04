package com.merchant.intelligence.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentMethodAnalyticsController {

    private final PaymentMethodAnalyticsService paymentMethodAnalyticsService;

    public PaymentMethodAnalyticsController(
            PaymentMethodAnalyticsService paymentMethodAnalyticsService) {

        this.paymentMethodAnalyticsService =
                paymentMethodAnalyticsService;
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<Map<String, Object>>>
    getPaymentMethodAnalytics() {

        return ResponseEntity.ok(
                paymentMethodAnalyticsService
                        .getPaymentMethodAnalytics()
        );
    }
}