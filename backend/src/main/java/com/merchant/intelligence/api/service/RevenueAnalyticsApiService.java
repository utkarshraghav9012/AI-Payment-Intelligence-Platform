package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RevenueAnalyticsApiService {

    public Map<String, Object> analyze(List<TransactionInput> transactions) {

        long totalTransactions = transactions.size();

        long successfulTransactions = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .count();

        long failedTransactions = transactions.stream()
                .filter(t -> "FAILED".equalsIgnoreCase(t.getStatus()))
                .count();

        double totalTransactionValue = transactions.stream()
                .filter(t -> t.getAmount() != null)
                .mapToDouble(TransactionInput::getAmount)
                .sum();

        double revenueAtRisk = transactions.stream()
                .filter(t -> "FAILED".equalsIgnoreCase(t.getStatus()))
                .filter(t -> t.getAmount() != null)
                .mapToDouble(TransactionInput::getAmount)
                .sum();

        double successRate = totalTransactions == 0
                ? 0.0
                : successfulTransactions * 100.0 / totalTransactions;

        double failureRate = totalTransactions == 0
                ? 0.0
                : failedTransactions * 100.0 / totalTransactions;

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalTransactions", totalTransactions);
        result.put("successfulTransactions", successfulTransactions);
        result.put("failedTransactions", failedTransactions);
        result.put("totalTransactionValue", round(totalTransactionValue));
        result.put("revenueAtRisk", round(revenueAtRisk));
        result.put("successRate", round(successRate));
        result.put("failureRate", round(failureRate));

        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}