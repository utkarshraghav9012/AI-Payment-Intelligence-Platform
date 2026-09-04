package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentFailureAnalysisService {

    private final TransactionRepository transactionRepository;

    public PaymentFailureAnalysisService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public List<Map<String, Object>> getPaymentFailureAnalysis() {

        return transactionRepository.findAll()
                .stream()
                .filter(transaction ->
                        "FAILED".equalsIgnoreCase(
                                transaction.getStatus()
                        ))
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getPaymentMethod()
                                + "|" + transaction.getFailureReason()
                ))
                .entrySet()
                .stream()
                .map(entry -> {

                    List<Transaction> transactions = entry.getValue();

                    String[] parts = entry.getKey().split("\\|", 2);

                    String paymentMethod = parts[0];
                    String failureReason = parts[1];

                    long failedCount = transactions.size();

                    double revenueAtRisk = transactions.stream()
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    return Map.<String, Object>of(
                            "paymentMethod", paymentMethod,
                            "failureReason", failureReason,
                            "failedCount", failedCount,
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