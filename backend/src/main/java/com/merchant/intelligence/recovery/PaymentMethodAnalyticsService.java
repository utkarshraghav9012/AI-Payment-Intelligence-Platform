package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentMethodAnalyticsService {

    private final TransactionRepository transactionRepository;

    public PaymentMethodAnalyticsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public List<Map<String, Object>> getPaymentMethodAnalytics() {

        return transactionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Transaction::getPaymentMethod
                ))
                .entrySet()
                .stream()
                .map(entry -> {

                    List<Transaction> transactions = entry.getValue();

                    long totalTransactions = transactions.size();

                    long failedTransactions = transactions.stream()
                            .filter(transaction ->
                                    "FAILED".equalsIgnoreCase(
                                            transaction.getStatus()
                                    ))
                            .count();

                    double transactionValue = transactions.stream()
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    double revenueAtRisk = transactions.stream()
                            .filter(transaction ->
                                    "FAILED".equalsIgnoreCase(
                                            transaction.getStatus()
                                    ))
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    double failureRate = totalTransactions == 0
                            ? 0.0
                            : failedTransactions * 100.0
                            / totalTransactions;

                    return Map.<String, Object>of(
                            "paymentMethod", entry.getKey(),
                            "totalTransactions", totalTransactions,
                            "failedTransactions", failedTransactions,
                            "transactionValue", transactionValue,
                            "revenueAtRisk", revenueAtRisk,
                            "failureRate",
                            Math.round(failureRate * 100.0) / 100.0
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