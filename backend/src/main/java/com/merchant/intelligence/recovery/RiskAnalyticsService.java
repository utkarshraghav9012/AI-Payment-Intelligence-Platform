package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskAnalyticsService {

    private final TransactionRepository transactionRepository;
    private final RiskSignalService riskSignalService;

    public RiskAnalyticsService(
            TransactionRepository transactionRepository,
            RiskSignalService riskSignalService) {

        this.transactionRepository = transactionRepository;
        this.riskSignalService = riskSignalService;
    }

    public Map<String, Object> getRiskAnalytics() {

        List<Transaction> transactions =
                transactionRepository.findAll();

        Map<String, List<Transaction>> groupedByRisk =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            Map<String, Object> risk =
                    riskSignalService.calculateRiskSignals(
                            transaction.getTransactionId()
                    );

            String riskLevel =
                    (String) risk.get("riskLevel");

            groupedByRisk
                    .computeIfAbsent(
                            riskLevel,
                            key -> new ArrayList<>()
                    )
                    .add(transaction);
        }

        List<Map<String, Object>> riskBreakdown =
                groupedByRisk.entrySet()
                        .stream()
                        .map(entry -> {

                            List<Transaction> group =
                                    entry.getValue();

                            double transactionValue =
                                    group.stream()
                                            .mapToDouble(
                                                    Transaction::getAmount
                                            )
                                            .sum();

                            long failedTransactions =
                                    group.stream()
                                            .filter(transaction ->
                                                    "FAILED".equalsIgnoreCase(
                                                            transaction.getStatus()
                                                    ))
                                            .count();

                            return Map.<String, Object>of(
                                    "riskLevel",
                                    entry.getKey(),

                                    "transactionCount",
                                    group.size(),

                                    "transactionValue",
                                    transactionValue,

                                    "failedTransactions",
                                    failedTransactions
                            );
                        })
                        .sorted(
                                Comparator.comparing(
                                        item -> (String)
                                                item.get("riskLevel")
                                )
                        )
                        .toList();

        long highRiskTransactions =
                groupedByRisk
                        .getOrDefault(
                                "HIGH",
                                Collections.emptyList()
                        )
                        .size();

        long mediumRiskTransactions =
                groupedByRisk
                        .getOrDefault(
                                "MEDIUM",
                                Collections.emptyList()
                        )
                        .size();

        long lowRiskTransactions =
                groupedByRisk
                        .getOrDefault(
                                "LOW",
                                Collections.emptyList()
                        )
                        .size();

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalTransactions",
                transactions.size()
        );

        result.put(
                "highRiskTransactions",
                highRiskTransactions
        );

        result.put(
                "mediumRiskTransactions",
                mediumRiskTransactions
        );

        result.put(
                "lowRiskTransactions",
                lowRiskTransactions
        );

        result.put(
                "riskBreakdown",
                riskBreakdown
        );

        return result;
    }
}