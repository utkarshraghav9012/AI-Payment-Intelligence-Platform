package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import org.springframework.stereotype.Service;

@Service
public class RecoveryService {

    public String recommend(Transaction transaction) {

        if (!"FAILED".equalsIgnoreCase(transaction.getStatus())) {
            return "No recovery action required.";
        }

        String reason = transaction.getFailureReason();

        if (reason == null || reason.isBlank()) {
            return "Retry payment.";
        }

        return switch (reason.toUpperCase()) {

            case "TIMEOUT" ->
                    "Retry payment after a short delay.";

            case "BANK_DECLINED" ->
                    "Ask customer to try another payment method.";

            case "INSUFFICIENT_FUNDS" ->
                    "Ask customer to use another payment method.";

            case "NETWORK_ERROR" ->
                    "Retry payment automatically.";

            default ->
                    "Review the payment failure and retry using a suitable recovery action.";
        };
    }
}