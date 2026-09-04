package com.merchant.intelligence.api.service;

import com.merchant.intelligence.api.dto.AnalyticsRequest;
import com.merchant.intelligence.api.dto.TransactionInput;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimeAnalyticsService {

    public Map<String, Object> analyze(
            List<TransactionInput> transactions,
            AnalyticsRequest request) {

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "transactions cannot be null"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "request cannot be null"
            );
        }

        String period = request.getPeriod();

        if (period == null || period.isBlank()) {
            period = "MONTH";
        }

        period = period.toUpperCase();

        LocalDate currentStart;
        LocalDate currentEnd;

        switch (period) {

            case "TODAY":
                currentStart = LocalDate.now();
                currentEnd = currentStart;
                break;

            case "WEEK":
                currentStart = LocalDate.now()
                        .with(TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        ));

                currentEnd = currentStart.plusDays(6);
                break;

            case "MONTH":
                currentStart = LocalDate.now()
                        .with(TemporalAdjusters.firstDayOfMonth());

                currentEnd = LocalDate.now()
                        .with(TemporalAdjusters.lastDayOfMonth());
                break;

            case "YEAR":
                currentStart = LocalDate.now()
                        .with(TemporalAdjusters.firstDayOfYear());

                currentEnd = LocalDate.now()
                        .with(TemporalAdjusters.lastDayOfYear());
                break;

            case "CUSTOM":

                if (request.getStartDate() == null
                        || request.getEndDate() == null) {

                    throw new IllegalArgumentException(
                            "startDate and endDate are required for CUSTOM period."
                    );
                }

                if (request.getStartDate()
                        .isAfter(request.getEndDate())) {

                    throw new IllegalArgumentException(
                            "startDate cannot be after endDate."
                    );
                }

                currentStart = request.getStartDate();
                currentEnd = request.getEndDate();
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid period. Use TODAY, WEEK, MONTH, YEAR or CUSTOM."
                );
        }

        long numberOfDays =
                java.time.temporal.ChronoUnit.DAYS.between(
                        currentStart,
                        currentEnd
                ) + 1;

        LocalDate previousEnd =
                currentStart.minusDays(1);

        LocalDate previousStart =
                previousEnd.minusDays(numberOfDays - 1);

        List<TransactionInput> currentTransactions =
                filterTransactions(
                        transactions,
                        currentStart,
                        currentEnd
                );

        List<TransactionInput> previousTransactions =
                filterTransactions(
                        transactions,
                        previousStart,
                        previousEnd
                );

        Map<String, Object> current =
                calculateMetrics(currentTransactions);

        Map<String, Object> previous =
                calculateMetrics(previousTransactions);

        Map<String, Object> comparison =
                buildComparison(
                        current,
                        previous
                );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("period", period);

        result.put(
                "currentStart",
                currentStart
        );

        result.put(
                "currentEnd",
                currentEnd
        );

        result.put(
                "previousStart",
                previousStart
        );

        result.put(
                "previousEnd",
                previousEnd
        );

        result.put(
                "current",
                current
        );

        result.put(
                "previous",
                previous
        );

        result.put(
                "comparison",
                comparison
        );

        return result;
    }

    private List<TransactionInput> filterTransactions(
            List<TransactionInput> transactions,
            LocalDate start,
            LocalDate end) {

        LocalDateTime startDateTime =
                start.atStartOfDay();

        LocalDateTime endDateTime =
                end.plusDays(1).atStartOfDay();

        return transactions.stream()
                .filter(transaction ->
                        transaction.getCreatedAt() != null
                )
                .filter(transaction ->
                        !transaction.getCreatedAt()
                                .isBefore(startDateTime)
                )
                .filter(transaction ->
                        transaction.getCreatedAt()
                                .isBefore(endDateTime)
                )
                .toList();
    }

    private Map<String, Object> calculateMetrics(
            List<TransactionInput> transactions) {

        long totalTransactions =
                transactions.size();

        long successfulTransactions =
                transactions.stream()
                        .filter(transaction ->
                                "SUCCESS".equalsIgnoreCase(
                                        transaction.getStatus()
                                )
                        )
                        .count();

        long failedTransactions =
                transactions.stream()
                        .filter(transaction ->
                                "FAILED".equalsIgnoreCase(
                                        transaction.getStatus()
                                )
                        )
                        .count();

        double totalTransactionValue =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getAmount() != null
                        )
                        .mapToDouble(
                                TransactionInput::getAmount
                        )
                        .sum();

        double revenue =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getAmount() != null
                        )
                        .filter(transaction ->
                                "SUCCESS".equalsIgnoreCase(
                                        transaction.getStatus()
                                )
                        )
                        .mapToDouble(
                                TransactionInput::getAmount
                        )
                        .sum();

        double revenueLost =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getAmount() != null
                        )
                        .filter(transaction ->
                                "FAILED".equalsIgnoreCase(
                                        transaction.getStatus()
                                )
                        )
                        .mapToDouble(
                                TransactionInput::getAmount
                        )
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

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalTransactions",
                totalTransactions
        );

        result.put(
                "successfulTransactions",
                successfulTransactions
        );

        result.put(
                "failedTransactions",
                failedTransactions
        );

        result.put(
                "totalTransactionValue",
                round(totalTransactionValue)
        );

        result.put(
                "revenue",
                round(revenue)
        );

        result.put(
                "revenueLost",
                round(revenueLost)
        );

        result.put(
                "successRate",
                round(successRate)
        );

        result.put(
                "failureRate",
                round(failureRate)
        );

        return result;
    }

    private Map<String, Object> buildComparison(
            Map<String, Object> current,
            Map<String, Object> previous) {

        Map<String, Object> comparison =
                new LinkedHashMap<>();

        addComparison(
                comparison,
                "revenue",
                current,
                previous
        );

        addComparison(
                comparison,
                "totalTransactions",
                current,
                previous
        );

        addComparison(
                comparison,
                "successfulTransactions",
                current,
                previous
        );

        addComparison(
                comparison,
                "failedTransactions",
                current,
                previous
        );

        addComparison(
                comparison,
                "revenueLost",
                current,
                previous
        );

        addComparison(
                comparison,
                "successRate",
                current,
                previous
        );

        addComparison(
                comparison,
                "failureRate",
                current,
                previous
        );

        return comparison;
    }

    private void addComparison(
            Map<String, Object> comparison,
            String metric,
            Map<String, Object> current,
            Map<String, Object> previous) {

        double currentValue =
                ((Number) current.get(metric))
                        .doubleValue();

        double previousValue =
                ((Number) previous.get(metric))
                        .doubleValue();

        double difference =
                currentValue - previousValue;

        double percentageChange;

        if (previousValue == 0) {

            percentageChange =
                    currentValue == 0
                            ? 0.0
                            : 100.0;

        } else {

            percentageChange =
                    (difference / previousValue) * 100.0;
        }

        Map<String, Object> metricResult =
                new LinkedHashMap<>();

        metricResult.put(
                "current",
                round(currentValue)
        );

        metricResult.put(
                "previous",
                round(previousValue)
        );

        metricResult.put(
                "difference",
                round(difference)
        );

        metricResult.put(
                "percentageChange",
                round(percentageChange)
        );

        comparison.put(
                metric,
                metricResult
        );
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}