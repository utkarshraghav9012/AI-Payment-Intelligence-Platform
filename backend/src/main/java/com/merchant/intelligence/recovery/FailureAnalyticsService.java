package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FailureAnalyticsService {

    private final TransactionRepository transactionRepository;

    public FailureAnalyticsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public List<Map<String, Object>> getFailureAnalytics() {

        return transactionRepository.findAll()
                .stream()
                .filter(transaction ->
                        "FAILED".equalsIgnoreCase(transaction.getStatus()))
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getFailureReason() == null
                                ? "UNKNOWN"
                                : transaction.getFailureReason()
                ))
                .entrySet()
                .stream()
                .map(entry -> {

                    List<Transaction> transactions = entry.getValue();

                    double revenueAtRisk = transactions.stream()
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    return Map.<String, Object>of(
                            "failureReason", entry.getKey(),
                            "failedCount", transactions.size(),
                            "revenueAtRisk", revenueAtRisk
                    );
                })
                .sorted(
                        Comparator.comparingDouble(
                                item -> -((Number) item.get("revenueAtRisk"))
                                        .doubleValue()
                        )
                )
                .toList();
    }
}