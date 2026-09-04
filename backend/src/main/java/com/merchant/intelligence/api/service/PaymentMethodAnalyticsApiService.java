package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentMethodAnalyticsApiService {

    public Map<String, Object> analyze(
            List<TransactionInput> transactions) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions are required."
            );
        }

        List<Map<String, Object>> methods =
                transactions.stream()
                        .filter(t -> t.getPaymentMethod() != null)
                        .collect(Collectors.groupingBy(
                                TransactionInput::getPaymentMethod,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ))
                        .entrySet()
                        .stream()
                        .map(entry -> {

                            List<TransactionInput> items =
                                    entry.getValue();

                            long total = items.size();

                            long success = items.stream()
                                    .filter(t ->
                                            "SUCCESS".equalsIgnoreCase(
                                                    t.getStatus()
                                            ))
                                    .count();

                            long failed = items.stream()
                                    .filter(t ->
                                            "FAILED".equalsIgnoreCase(
                                                    t.getStatus()
                                            ))
                                    .count();

                            double revenue = items.stream()
                                    .filter(t ->
                                            "SUCCESS".equalsIgnoreCase(
                                                    t.getStatus()
                                            ))
                                    .filter(t -> t.getAmount() != null)
                                    .mapToDouble(
                                            TransactionInput::getAmount
                                    )
                                    .sum();

                            double revenueLost = items.stream()
                                    .filter(t ->
                                            "FAILED".equalsIgnoreCase(
                                                    t.getStatus()
                                            ))
                                    .filter(t -> t.getAmount() != null)
                                    .mapToDouble(
                                            TransactionInput::getAmount
                                    )
                                    .sum();

                            double successRate =
                                    total == 0
                                            ? 0.0
                                            : success * 100.0 / total;

                            double failureRate =
                                    total == 0
                                            ? 0.0
                                            : failed * 100.0 / total;

                            return Map.<String, Object>of(
                                    "paymentMethod",
                                    entry.getKey(),

                                    "totalTransactions",
                                    total,

                                    "successfulTransactions",
                                    success,

                                    "failedTransactions",
                                    failed,

                                    "revenue",
                                    round(revenue),

                                    "revenueLost",
                                    round(revenueLost),

                                    "successRate",
                                    round(successRate),

                                    "failureRate",
                                    round(failureRate)
                            );
                        })
                        .sorted(
                                Comparator.comparingLong(
                                        item -> -((Number)
                                                item.get(
                                                        "totalTransactions"
                                                )).longValue()
                                )
                        )
                        .toList();

        String mostUsed =
                methods.isEmpty()
                        ? "N/A"
                        : methods.get(0)
                                .get("paymentMethod")
                                .toString();

        String highestFailure =
                methods.stream()
                        .max(
                                Comparator.comparingDouble(
                                        item -> ((Number)
                                                item.get(
                                                        "failureRate"
                                                )).doubleValue()
                                )
                        )
                        .map(item ->
                                item.get("paymentMethod").toString())
                        .orElse("N/A");

        String highestLoss =
                methods.stream()
                        .max(
                                Comparator.comparingDouble(
                                        item -> ((Number)
                                                item.get(
                                                        "revenueLost"
                                                )).doubleValue()
                                )
                        )
                        .map(item ->
                                item.get("paymentMethod").toString())
                        .orElse("N/A");

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "mostUsedPaymentMethod",
                mostUsed
        );

        result.put(
                "highestFailurePaymentMethod",
                highestFailure
        );

        result.put(
                "highestRevenueLossPaymentMethod",
                highestLoss
        );

        result.put(
                "methods",
                methods
        );

        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}