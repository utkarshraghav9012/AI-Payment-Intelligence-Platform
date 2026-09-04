package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpectedRecoveryService {

    private final TransactionRepository transactionRepository;
    private final RecoveryProbabilityService recoveryProbabilityService;

    public ExpectedRecoveryService(
            TransactionRepository transactionRepository,
            RecoveryProbabilityService recoveryProbabilityService) {

        this.transactionRepository = transactionRepository;
        this.recoveryProbabilityService = recoveryProbabilityService;
    }

    public List<Map<String, Object>> getExpectedRecoveryValues() {

        List<Transaction> failedTransactions =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                "FAILED".equalsIgnoreCase(
                                        transaction.getStatus()))
                        .toList();

        List<Map<String, Object>> results =
                new ArrayList<>();

        for (Transaction transaction : failedTransactions) {

            double recoveryProbability =
                    recoveryProbabilityService.calculate(transaction);

            double expectedRecoveryValue =
                    transaction.getAmount()
                            * recoveryProbability;

            String recommendation;

            if (expectedRecoveryValue >= 10000
                    && recoveryProbability >= 0.50) {

                recommendation = "HIGH_PRIORITY";

            } else if (expectedRecoveryValue >= 5000
                    && recoveryProbability >= 0.40) {

                recommendation = "MEDIUM_PRIORITY";

            } else {

                recommendation = "LOW_PRIORITY";
            }

            Map<String, Object> result =
                    new LinkedHashMap<>();

            result.put(
                    "transactionId",
                    transaction.getTransactionId()
            );

            result.put(
                    "amount",
                    transaction.getAmount()
            );

            result.put(
                    "failureReason",
                    transaction.getFailureReason()
            );

            result.put(
                    "retryCount",
                    transaction.getRetryCount()
            );

            result.put(
                    "recoveryProbability",
                    recoveryProbability
            );

            result.put(
                    "expectedRecoveryValue",
                    Math.round(
                            expectedRecoveryValue * 100.0
                    ) / 100.0
            );

            result.put(
                    "recommendation",
                    recommendation
            );

            results.add(result);
        }

        results.sort(
                Comparator.comparingDouble(
                        item -> -((Number)
                                item.get("expectedRecoveryValue"))
                                .doubleValue()
                )
        );

        return results;
    }
}