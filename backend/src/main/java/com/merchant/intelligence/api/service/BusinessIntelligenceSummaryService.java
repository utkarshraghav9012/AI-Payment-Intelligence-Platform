package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessIntelligenceSummaryService {

    public Map<String, Object> analyze(
            List<TransactionInput> transactions) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions are required."
            );
        }

        long totalTransactions = transactions.size();

        long successfulTransactions =
                transactions.stream()
                        .filter(t ->
                                "SUCCESS".equalsIgnoreCase(
                                        t.getStatus()))
                        .count();

        long failedTransactions =
                transactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()))
                        .count();

        double totalValue =
                transactions.stream()
                        .filter(t -> t.getAmount() != null)
                        .mapToDouble(TransactionInput::getAmount)
                        .sum();

        double revenue =
                transactions.stream()
                        .filter(t ->
                                "SUCCESS".equalsIgnoreCase(
                                        t.getStatus()))
                        .filter(t -> t.getAmount() != null)
                        .mapToDouble(TransactionInput::getAmount)
                        .sum();

        double revenueLost =
                transactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()))
                        .filter(t -> t.getAmount() != null)
                        .mapToDouble(TransactionInput::getAmount)
                        .sum();

        double successRate =
                totalTransactions == 0
                        ? 0.0
                        : successfulTransactions * 100.0
                        / totalTransactions;

        double failureRate =
                totalTransactions == 0
                        ? 0.0
                        : failedTransactions * 100.0
                        / totalTransactions;

        // Most used payment method
        String mostUsedPaymentMethod =
                transactions.stream()
                        .filter(t -> t.getPaymentMethod() != null)
                        .collect(Collectors.groupingBy(
                                TransactionInput::getPaymentMethod,
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");

        // Failure reasons
        Map<String, Long> failedByReason =
                transactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()))
                        .filter(t -> t.getFailureReason() != null)
                        .collect(Collectors.groupingBy(
                                TransactionInput::getFailureReason,
                                Collectors.counting()
                        ));

        String topFailureReason =
                failedByReason.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");

        // Revenue lost by payment method
        Map<String, Double> revenueLostByMethod =
                transactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()))
                        .filter(t -> t.getPaymentMethod() != null)
                        .filter(t -> t.getAmount() != null)
                        .collect(Collectors.groupingBy(
                                TransactionInput::getPaymentMethod,
                                Collectors.summingDouble(
                                        TransactionInput::getAmount)
                        ));

        String highestRevenueLossPaymentMethod =
                revenueLostByMethod.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");

        double highestMethodRevenueLoss =
                revenueLostByMethod.values()
                        .stream()
                        .max(Double::compareTo)
                        .orElse(0.0);

        // Group transactions by hour
        Map<Integer, Long> transactionsByHour =
                transactions.stream()
                        .filter(t -> t.getCreatedAt() != null)
                        .collect(Collectors.groupingBy(
                                t -> t.getCreatedAt().getHour(),
                                Collectors.counting()
                        ));

        // Find maximum transactions in any hour
        long maxTransactionsPerHour =
                transactionsByHour.values()
                        .stream()
                        .max(Long::compareTo)
                        .orElse(0L);

        // Return ALL tied peak sales hours
        List<String> peakSalesHours =
                transactionsByHour.entrySet()
                        .stream()
                        .filter(entry ->
                                entry.getValue()
                                        == maxTransactionsPerHour)
                        .map(entry ->
                                formatHour(entry.getKey()))
                        .sorted()
                        .toList();

        // Business insights
        List<String> insights = new ArrayList<>();

        if (revenueLost > 0) {
            insights.add(
                    "INR " + round(revenueLost)
                            + " revenue was lost due to failed payments."
            );
        }

        if (!"N/A".equals(topFailureReason)) {
            insights.add(
                    "Top failure reason is "
                            + topFailureReason + "."
            );
        }

        if (!"N/A".equals(highestRevenueLossPaymentMethod)) {
            insights.add(
                    highestRevenueLossPaymentMethod
                            + " has the highest payment-method revenue loss."
            );
        }

        if (!peakSalesHours.isEmpty()) {
            insights.add(
                    "Peak transaction activity occurs during "
                            + String.join(", ", peakSalesHours)
                            + "."
            );
        }

        if (insights.isEmpty()) {
            insights.add(
                    "No significant business issue detected from the supplied transactions."
            );
        }

        // Business recommendations
        List<String> recommendations = new ArrayList<>();

        if (failureRate > 10) {
            recommendations.add(
                    "Investigate failed payment causes because the failure rate is "
                            + round(failureRate) + "%."
            );
        }

        if (!"N/A".equals(highestRevenueLossPaymentMethod)) {
            recommendations.add(
                    "Prioritize "
                            + highestRevenueLossPaymentMethod
                            + " failure issues because it contributes the highest revenue loss."
            );
        }

        if (!"N/A".equals(topFailureReason)) {
            recommendations.add(
                    "Investigate "
                            + topFailureReason
                            + " failures and improve handling during high-traffic periods."
            );
        }

        if (recommendations.isEmpty()) {
            recommendations.add(
                    "Continue monitoring payment performance and transaction trends."
            );
        }

        // Overview
        Map<String, Object> overview =
                new LinkedHashMap<>();

        overview.put(
                "totalTransactions",
                totalTransactions
        );

        overview.put(
                "successfulTransactions",
                successfulTransactions
        );

        overview.put(
                "failedTransactions",
                failedTransactions
        );

        overview.put(
                "totalTransactionValue",
                round(totalValue)
        );

        overview.put(
                "revenue",
                round(revenue)
        );

        overview.put(
                "revenueLost",
                round(revenueLost)
        );

        overview.put(
                "successRate",
                round(successRate)
        );

        overview.put(
                "failureRate",
                round(failureRate)
        );

        // Final response
        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "overview",
                overview
        );

        result.put(
                "mostUsedPaymentMethod",
                mostUsedPaymentMethod
        );

        result.put(
                "topFailureReason",
                topFailureReason
        );

        result.put(
                "highestRevenueLossPaymentMethod",
                highestRevenueLossPaymentMethod
        );

        result.put(
                "highestMethodRevenueLoss",
                round(highestMethodRevenueLoss)
        );

        result.put(
                "peakSalesHours",
                peakSalesHours
        );

        result.put(
                "insights",
                insights
        );

        result.put(
                "recommendations",
                recommendations
        );

        return result;
    }

    private String formatHour(int hour) {

        int endHour = (hour + 1) % 24;

        return String.format(
                "%02d:00-%02d:00",
                hour,
                endHour
        );
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}