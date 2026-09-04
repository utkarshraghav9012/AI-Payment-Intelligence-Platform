package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecoveryProbabilityService {

    private final RecoveryScoringService recoveryScoringService;

    public RecoveryProbabilityService(
            RecoveryScoringService recoveryScoringService) {
        this.recoveryScoringService = recoveryScoringService;
    }

    public double calculate(Transaction transaction) {

        Map<String, Object> result =
                recoveryScoringService.calculateRecoveryScore(
                        transaction.getTransactionId()
                );

        Number probability =
                (Number) result.get("recoveryProbability");

        return probability.doubleValue();
    }
}