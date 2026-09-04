package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RevenueAnalyticsService {

    private final TransactionRepository transactionRepository;

    public RevenueAnalyticsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> getRevenueAnalytics() {

        List<Transaction> transactions =
                transactionRepository.findAll();

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

        double successRate = totalTransactions == 0
                ? 0
                : (successfulTransactions * 100.0) / totalTransactions;

        double failureRate = totalTransactions == 0
                ? 0
                : (failedTransactions * 100.0) / totalTransactions;

        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalTransactions", totalTransactions);
        analytics.put("successfulTransactions", successfulTransactions);
        analytics.put("failedTransactions", failedTransactions);
        analytics.put("totalTransactionValue", totalTransactionValue);
        analytics.put("revenueAtRisk", revenueAtRisk);
        analytics.put("successRate", Math.round(successRate * 100.0) / 100.0);
        analytics.put("failureRate", Math.round(failureRate * 100.0) / 100.0);

        return analytics;
    }
}