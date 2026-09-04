package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RiskSignalService {

    private final TransactionRepository transactionRepository;

    public RiskSignalService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> calculateRiskSignals(
            String transactionId) {

        List<Transaction> transactions =
                transactionRepository.findAll();

        Transaction target = transactions.stream()
                .filter(transaction ->
                        transaction.getTransactionId()
                                .equals(transactionId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found: " + transactionId
                        ));

        List<String> signals = new ArrayList<>();

        int riskScore = 0;

        /*
         * ---------------------------------------------------------
         * 1. MERCHANT AMOUNT ANOMALY
         * ---------------------------------------------------------
         *
         * Amount anomaly is a behavioral signal when compared
         * with the merchant's normal transaction distribution.
         */

        List<Double> merchantAmounts =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getMerchantId()
                                        .equals(target.getMerchantId()))
                        .map(Transaction::getAmount)
                        .sorted()
                        .toList();

        double merchantAverage =
                merchantAmounts.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(target.getAmount());

        double percentileRank =
                calculatePercentile(
                        merchantAmounts,
                        target.getAmount()
                );

        if (percentileRank >= 95) {

            signals.add("VERY_HIGH_AMOUNT");
            riskScore += 30;

        } else if (percentileRank >= 85) {

            signals.add("HIGH_AMOUNT");
            riskScore += 20;

        } else if (percentileRank >= 70) {

            signals.add("ELEVATED_AMOUNT");
            riskScore += 10;
        }

        /*
         * ---------------------------------------------------------
         * 2. CUSTOMER FAILURE BEHAVIOR
         * ---------------------------------------------------------
         */

        long customerTransactions =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getCustomerId()
                                        .equals(target.getCustomerId()))
                        .count();

        long customerFailures =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getCustomerId()
                                        .equals(target.getCustomerId()))
                        .filter(transaction ->
                                "FAILED".equalsIgnoreCase(
                                        transaction.getStatus()))
                        .count();

        double customerFailureRate =
                customerTransactions == 0
                        ? 0.0
                        : customerFailures * 100.0
                        / customerTransactions;

        if (customerFailures >= 3) {

            signals.add("REPEATED_CUSTOMER_FAILURE");
            riskScore += 30;

        } else if (customerFailures >= 2) {

            signals.add("CUSTOMER_FAILURE_PATTERN");
            riskScore += 15;
        }

        /*
         * ---------------------------------------------------------
         * 3. CUSTOMER ACTIVITY
         * ---------------------------------------------------------
         */

        if (customerTransactions >= 10) {

            signals.add("VERY_HIGH_CUSTOMER_ACTIVITY");
            riskScore += 25;

        } else if (customerTransactions >= 5) {

            signals.add("HIGH_CUSTOMER_ACTIVITY");
            riskScore += 15;

        } else if (customerTransactions >= 3) {

            signals.add("ELEVATED_CUSTOMER_ACTIVITY");
            riskScore += 8;
        }

        /*
         * ---------------------------------------------------------
         * 4. RETRY BEHAVIOR
         * ---------------------------------------------------------
         */

        if (target.getRetryCount() >= 3) {

            signals.add("EXCESSIVE_RETRIES");
            riskScore += 25;

        } else if (target.getRetryCount() >= 2) {

            signals.add("MULTIPLE_RETRIES");
            riskScore += 15;

        } else if (target.getRetryCount() == 1) {

            signals.add("RETRIED_TRANSACTION");
            riskScore += 5;
        }

        /*
         * ---------------------------------------------------------
         * 5. REPEATED FAILURE REASON
         * ---------------------------------------------------------
         */

        if (target.getFailureReason() != null) {

            long sameFailureCount =
                    transactions.stream()
                            .filter(transaction ->
                                    transaction.getCustomerId()
                                            .equals(target.getCustomerId()))
                            .filter(transaction ->
                                    target.getFailureReason()
                                            .equalsIgnoreCase(
                                                    transaction.getFailureReason()))
                            .count();

            if (sameFailureCount >= 3) {

                signals.add("REPEATED_FAILURE_PATTERN");
                riskScore += 20;

            } else if (sameFailureCount >= 2) {

                signals.add("RECURRING_FAILURE_REASON");
                riskScore += 10;
            }
        }

        /*
         * ---------------------------------------------------------
         * 6. FAILED TRANSACTION AMOUNT
         * ---------------------------------------------------------
         *
         * A high-value failed transaction represents financial
         * exposure, NOT fraud by itself.
         *
         * Therefore this signal is kept for business analytics
         * but contributes ZERO points to the behavioral risk score.
         */

        if ("FAILED".equalsIgnoreCase(target.getStatus())
                && target.getAmount() >= 20000) {

            signals.add("HIGH_VALUE_FAILED_TRANSACTION");
        }

        /*
         * ---------------------------------------------------------
         * FINAL RISK SCORE
         * ---------------------------------------------------------
         */

        riskScore = Math.min(riskScore, 100);

        String riskLevel;

        if (riskScore >= 70) {

            riskLevel = "HIGH";

        } else if (riskScore >= 40) {

            riskLevel = "MEDIUM";

        } else {

            riskLevel = "LOW";
        }

        /*
         * ---------------------------------------------------------
         * RESPONSE
         * ---------------------------------------------------------
         */

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "transactionId",
                target.getTransactionId()
        );

        result.put(
                "customerId",
                target.getCustomerId()
        );

        result.put(
                "merchantId",
                target.getMerchantId()
        );

        result.put(
                "amount",
                target.getAmount()
        );

        result.put(
                "riskScore",
                riskScore
        );

        result.put(
                "riskLevel",
                riskLevel
        );

        result.put(
                "signals",
                signals
        );

        result.put(
                "customerTransactionCount",
                customerTransactions
        );

        result.put(
                "customerFailureCount",
                customerFailures
        );

        result.put(
                "customerFailureRate",
                Math.round(
                        customerFailureRate * 100.0
                ) / 100.0
        );

        result.put(
                "merchantAverageAmount",
                merchantAverage
        );

        result.put(
                "merchantAmountPercentile",
                Math.round(
                        percentileRank * 100.0
                ) / 100.0
        );

        return result;
    }

    /*
     * -------------------------------------------------------------
     * PERCENTILE CALCULATION
     * -------------------------------------------------------------
     */

    private double calculatePercentile(
            List<Double> values,
            double targetAmount) {

        if (values.isEmpty()) {
            return 0.0;
        }

        long valuesBelowOrEqual =
                values.stream()
                        .filter(value ->
                                value <= targetAmount)
                        .count();

        return valuesBelowOrEqual * 100.0
                / values.size();
    }
}