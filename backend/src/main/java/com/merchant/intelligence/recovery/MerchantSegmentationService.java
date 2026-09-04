package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantSegmentationService {

    private final TransactionRepository transactionRepository;

    public MerchantSegmentationService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public List<Map<String, Object>> getMerchantSegments() {

        return transactionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Transaction::getMerchantId
                ))
                .entrySet()
                .stream()
                .map(entry -> {

                    List<Transaction> transactions =
                            entry.getValue();

                    long totalTransactions =
                            transactions.size();

                    long successfulTransactions =
                            transactions.stream()
                                    .filter(transaction ->
                                            "SUCCESS".equalsIgnoreCase(
                                                    transaction.getStatus()
                                            ))
                                    .count();

                    long failedTransactions =
                            transactions.stream()
                                    .filter(transaction ->
                                            "FAILED".equalsIgnoreCase(
                                                    transaction.getStatus()
                                            ))
                                    .count();

                    double transactionValue =
                            transactions.stream()
                                    .mapToDouble(
                                            Transaction::getAmount
                                    )
                                    .sum();

                    double revenueAtRisk =
                            transactions.stream()
                                    .filter(transaction ->
                                            "FAILED".equalsIgnoreCase(
                                                    transaction.getStatus()
                                            ))
                                    .mapToDouble(
                                            Transaction::getAmount
                                    )
                                    .sum();

                    double failureRate =
                            totalTransactions == 0
                                    ? 0.0
                                    : failedTransactions * 100.0
                                    / totalTransactions;

                    String topFailureReason =
                            transactions.stream()
                                    .filter(transaction ->
                                            "FAILED".equalsIgnoreCase(
                                                    transaction.getStatus()
                                            ))
                                    .collect(Collectors.groupingBy(
                                            Transaction::getFailureReason,
                                            Collectors.counting()
                                    ))
                                    .entrySet()
                                    .stream()
                                    .max(
                                            Map.Entry.comparingByValue()
                                    )
                                    .map(Map.Entry::getKey)
                                    .orElse("NONE");

                    return Map.<String, Object>of(
                            "merchantId",
                            entry.getKey(),

                            "totalTransactions",
                            totalTransactions,

                            "successfulTransactions",
                            successfulTransactions,

                            "failedTransactions",
                            failedTransactions,

                            "transactionValue",
                            transactionValue,

                            "revenueAtRisk",
                            revenueAtRisk,

                            "failureRate",
                            Math.round(
                                    failureRate * 100.0
                            ) / 100.0,

                            "topFailureReason",
                            topFailureReason
                    );
                })
                .sorted(
                        Comparator.comparingDouble(
                                item -> -((Number)
                                        item.get("revenueAtRisk"))
                                        .doubleValue()
                        )
                )
                .toList();
    }
}