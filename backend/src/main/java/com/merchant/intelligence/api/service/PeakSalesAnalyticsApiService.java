package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PeakSalesAnalyticsApiService {

    public Map<String, Object> analyze(
            List<TransactionInput> transactions) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions are required."
            );
        }

        Map<Integer, List<TransactionInput>> groupedByHour =
                transactions.stream()
                        .filter(t -> t.getCreatedAt() != null)
                        .collect(Collectors.groupingBy(
                                t -> t.getCreatedAt().getHour()
                        ));

        List<Map<String, Object>> hourlyAnalytics =
                groupedByHour.entrySet()
                        .stream()
                        .map(entry -> {

                            int hour = entry.getKey();
                            List<TransactionInput> items = entry.getValue();

                            long totalTransactions = items.size();

                            long successfulTransactions =
                                    items.stream()
                                            .filter(t ->
                                                    "SUCCESS".equalsIgnoreCase(
                                                            t.getStatus()))
                                            .count();

                            long failedTransactions =
                                    items.stream()
                                            .filter(t ->
                                                    "FAILED".equalsIgnoreCase(
                                                            t.getStatus()))
                                            .count();

                            double revenue =
                                    items.stream()
                                            .filter(t ->
                                                    "SUCCESS".equalsIgnoreCase(
                                                            t.getStatus()))
                                            .filter(t -> t.getAmount() != null)
                                            .mapToDouble(TransactionInput::getAmount)
                                            .sum();

                            double revenueLost =
                                    items.stream()
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

                            return Map.<String, Object>of(
                                    "hour", hour,
                                    "time", formatHour(hour),
                                    "totalTransactions", totalTransactions,
                                    "successfulTransactions",
                                    successfulTransactions,
                                    "failedTransactions",
                                    failedTransactions,
                                    "successRate",
                                    round(successRate),
                                    "failureRate",
                                    round(failureRate),
                                    "revenue",
                                    round(revenue),
                                    "revenueLost",
                                    round(revenueLost)
                            );
                        })
                        .sorted(
                                Comparator.comparingInt(
                                        item -> ((Number)
                                                item.get("hour"))
                                                .intValue()
                                )
                        )
                        .toList();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put(
                "peakSalesHours",
                findPeakHours(
                        hourlyAnalytics,
                        "totalTransactions"
                )
        );

        result.put(
                "peakRevenueHours",
                findPeakHours(
                        hourlyAnalytics,
                        "revenue"
                )
        );

        result.put(
                "peakFailureHours",
                findPeakHours(
                        hourlyAnalytics,
                        "failedTransactions"
                )
        );

        result.put(
                "hourlyAnalytics",
                hourlyAnalytics
        );

        return result;
    }

    private List<Map<String, Object>> findPeakHours(
            List<Map<String, Object>> hourlyAnalytics,
            String metric) {

        if (hourlyAnalytics.isEmpty()) {
            return List.of();
        }

        double maximum =
                hourlyAnalytics.stream()
                        .mapToDouble(
                                item -> ((Number)
                                        item.get(metric))
                                        .doubleValue()
                        )
                        .max()
                        .orElse(0.0);

        return hourlyAnalytics.stream()
                .filter(
                        item -> ((Number)
                                item.get(metric))
                                .doubleValue() == maximum
                )
                .toList();
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