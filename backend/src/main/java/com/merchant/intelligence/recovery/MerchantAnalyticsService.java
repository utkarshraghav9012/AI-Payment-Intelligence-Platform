package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantAnalyticsService {

    private final TransactionRepository transactionRepository;
    private final RecoveryAttemptRepository recoveryAttemptRepository;

    public MerchantAnalyticsService(
            TransactionRepository transactionRepository,
            RecoveryAttemptRepository recoveryAttemptRepository) {

        this.transactionRepository = transactionRepository;
        this.recoveryAttemptRepository = recoveryAttemptRepository;
    }

    public Map<String, Object> getMerchantOverview() {

        List<Transaction> transactions =
                transactionRepository.findAll();

        List<RecoveryAttempt> recoveryAttempts =
                recoveryAttemptRepository.findAll();

        long totalTransactions = transactions.size();

        long successfulTransactions = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .count();

        long failedTransactions = transactions.stream()
                .filter(t -> "FAILED".equalsIgnoreCase(t.getStatus()))
                .count();

        double totalTransactionValue = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double revenueAtRisk = transactions.stream()
                .filter(t -> "FAILED".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double recoveredRevenue = recoveryAttempts.stream()
                .mapToDouble(RecoveryAttempt::getRecoveredRevenue)
                .sum();

        long recoveryAttemptsCount = recoveryAttempts.size();

        long successfulRecoveries = recoveryAttempts.stream()
                .filter(attempt ->
                        "SUCCESS".equalsIgnoreCase(attempt.getResult()))
                .count();

        double successRate = totalTransactions == 0
                ? 0.0
                : successfulTransactions * 100.0 / totalTransactions;

        double failureRate = totalTransactions == 0
                ? 0.0
                : failedTransactions * 100.0 / totalTransactions;

        double recoveryRate = recoveryAttemptsCount == 0
                ? 0.0
                : successfulRecoveries * 100.0
                / recoveryAttemptsCount;

        double remainingRevenueAtRisk =
                Math.max(revenueAtRisk - recoveredRevenue, 0.0);

        Map<String, Object> result = new HashMap<>();

        result.put("totalTransactions", totalTransactions);
        result.put("successfulTransactions", successfulTransactions);
        result.put("failedTransactions", failedTransactions);

        result.put(
                "totalTransactionValue",
                totalTransactionValue
        );

        result.put(
                "revenueAtRisk",
                revenueAtRisk
        );

        result.put(
                "recoveredRevenue",
                recoveredRevenue
        );

        result.put(
                "remainingRevenueAtRisk",
                remainingRevenueAtRisk
        );

        result.put(
                "successRate",
                Math.round(successRate * 100.0) / 100.0
        );

        result.put(
                "failureRate",
                Math.round(failureRate * 100.0) / 100.0
        );

        result.put(
                "recoveryAttempts",
                recoveryAttemptsCount
        );

        result.put(
                "successfulRecoveries",
                successfulRecoveries
        );

        result.put(
                "recoveryRate",
                Math.round(recoveryRate * 100.0) / 100.0
        );

        return result;
    }
}