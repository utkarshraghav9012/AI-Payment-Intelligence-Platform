package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FailureReasonAnalyticsApiService {

    public Map<String, Object> analyze(
            List<TransactionInput> transactions) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions are required."
            );
        }

        List<TransactionInput> failedTransactions =
                transactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()
                                ))
                        .toList();

        long totalFailed = failedTransactions.size();

        double totalRevenueLost =
                failedTransactions.stream()
                        .filter(t -> t.getAmount() != null)
                        .mapToDouble(TransactionInput::getAmount)
                        .sum();

        List<Map<String, Object>> reasons =
                failedTransactions.stream()
                        .filter(t -> t.getFailureReason() != null)
                        .collect(Collectors.groupingBy(
                                TransactionInput::getFailureReason,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ))
                        .entrySet()
                        .stream()
                        .map(entry -> {

                            List<TransactionInput> items =
                                    entry.getValue();

                            long failedCount = items.size();

                            double revenueLost =
                                    items.stream()
                                            .filter(t ->
                                                    t.getAmount() != null)
                                            .mapToDouble(
                                                    TransactionInput::getAmount
                                            )
                                            .sum();

                            double failureShare =
                                    totalFailed == 0
                                            ? 0.0
                                            : failedCount * 100.0
                                            / totalFailed;

                            return Map.<String, Object>of(
                                    "failureReason",
                                    entry.getKey(),

                                    "failedTransactions",
                                    failedCount,

                                    "failureShare",
                                    round(failureShare),

                                    "revenueLost",
                                    round(revenueLost)
                            );
                        })
                        .sorted(
                                Comparator.comparingLong(
                                        item -> -((Number)
                                                item.get(
                                                        "failedTransactions"
                                                )).longValue()
                                )
                        )
                        .toList();

        String topFailureReason =
                reasons.isEmpty()
                        ? "N/A"
                        : reasons.get(0)
                                .get("failureReason")
                                .toString();

        String highestRevenueLossReason =
                reasons.stream()
                        .max(
                                Comparator.comparingDouble(
                                        item -> ((Number)
                                                item.get(
                                                        "revenueLost"
                                                )).doubleValue()
                                )
                        )
                        .map(item ->
                                item.get("failureReason").toString())
                        .orElse("N/A");

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalFailedTransactions",
                totalFailed
        );

        result.put(
                "totalRevenueLost",
                round(totalRevenueLost)
        );

        result.put(
                "topFailureReason",
                topFailureReason
        );

        result.put(
                "highestRevenueLossReason",
                highestRevenueLossReason
        );

        result.put(
                "failureReasons",
                reasons
        );

        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}