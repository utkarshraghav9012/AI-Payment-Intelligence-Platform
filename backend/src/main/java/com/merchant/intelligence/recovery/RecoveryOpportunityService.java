package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecoveryOpportunityService {

    private final TransactionRepository transactionRepository;
    private final RecoveryProbabilityService recoveryProbabilityService;

    public RecoveryOpportunityService(
            TransactionRepository transactionRepository,
            RecoveryProbabilityService recoveryProbabilityService) {

        this.transactionRepository = transactionRepository;
        this.recoveryProbabilityService = recoveryProbabilityService;
    }

    public Map<String, Object> getRecoveryOpportunity() {

        List<Transaction> failedTransactions =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                "FAILED".equalsIgnoreCase(
                                        transaction.getStatus()))
                        .toList();

        double totalRevenueAtRisk =
                failedTransactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum();

        double expectedRecoverableValue = 0.0;

        for (Transaction transaction : failedTransactions) {

            double probability =
                    recoveryProbabilityService.calculate(
                            transaction
                    );

            expectedRecoverableValue +=
                    transaction.getAmount()
                            * probability;
        }

        double recoveryOpportunityRate =
                totalRevenueAtRisk == 0
                        ? 0.0
                        : expectedRecoverableValue
                        / totalRevenueAtRisk
                        * 100.0;

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "failedTransactions",
                failedTransactions.size()
        );

        result.put(
                "revenueAtRisk",
                round(totalRevenueAtRisk)
        );

        result.put(
                "expectedRecoverableValue",
                round(expectedRecoverableValue)
        );

        result.put(
                "recoveryOpportunityRate",
                round(recoveryOpportunityRate)
        );

        return result;
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}