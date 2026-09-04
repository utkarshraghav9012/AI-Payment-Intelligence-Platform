package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecoveryScoringService {

    private final TransactionRepository transactionRepository;

    public RecoveryScoringService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> calculateRecoveryScore(
            String transactionId) {

        Transaction target = transactionRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found: " + transactionId
                        ));

        List<Transaction> transactions =
                transactionRepository.findAll();

        /*
         * ---------------------------------------------------------
         * CUSTOMER HISTORY
         * ---------------------------------------------------------
         */

        List<Transaction> customerHistory =
                transactions.stream()
                        .filter(t ->
                                Objects.equals(
                                        t.getCustomerId(),
                                        target.getCustomerId()
                                ))
                        .filter(t ->
                                !Objects.equals(
                                        t.getTransactionId(),
                                        target.getTransactionId()
                                ))
                        .toList();

        long customerAttempts =
                customerHistory.size();

        long customerSuccesses =
                customerHistory.stream()
                        .filter(t ->
                                "SUCCESS".equalsIgnoreCase(
                                        t.getStatus()
                                ))
                        .count();

        long customerFailures =
                customerHistory.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()
                                ))
                        .count();

        double customerSuccessRate =
                customerAttempts == 0
                        ? 0.0
                        : customerSuccesses * 100.0
                        / customerAttempts;

        /*
         * ---------------------------------------------------------
         * PAYMENT METHOD HISTORY
         * ---------------------------------------------------------
         */

        List<Transaction> paymentMethodHistory =
                transactions.stream()
                        .filter(t ->
                                Objects.equals(
                                        t.getPaymentMethod(),
                                        target.getPaymentMethod()
                                ))
                        .filter(t ->
                                Objects.equals(
                                        t.getMerchantId(),
                                        target.getMerchantId()
                                ))
                        .toList();

        long methodAttempts =
                paymentMethodHistory.size();

        long methodSuccesses =
                paymentMethodHistory.stream()
                        .filter(t ->
                                "SUCCESS".equalsIgnoreCase(
                                        t.getStatus()
                                ))
                        .count();

        double paymentMethodSuccessRate =
                methodAttempts == 0
                        ? 0.0
                        : methodSuccesses * 100.0
                        / methodAttempts;

        /*
         * ---------------------------------------------------------
         * MERCHANT FAILURE PATTERN
         * ---------------------------------------------------------
         */

        List<Transaction> merchantTransactions =
                transactions.stream()
                        .filter(t ->
                                Objects.equals(
                                        t.getMerchantId(),
                                        target.getMerchantId()
                                ))
                        .toList();

        long merchantAttempts =
                merchantTransactions.size();

        long merchantFailures =
                merchantTransactions.stream()
                        .filter(t ->
                                "FAILED".equalsIgnoreCase(
                                        t.getStatus()
                                ))
                        .count();

        double merchantFailureRate =
                merchantAttempts == 0
                        ? 0.0
                        : merchantFailures * 100.0
                        / merchantAttempts;

        /*
         * ---------------------------------------------------------
         * BASE RECOVERY SCORE
         * ---------------------------------------------------------
         *
         * This is a business decision score, not a machine-learning
         * probability. It combines observable transaction behaviour.
         */

        double score = 0.0;

        List<String> signals = new ArrayList<>();

        /*
         * 1. FAILURE REASON
         */

        String failureReason =
                target.getFailureReason();

        if ("TIMEOUT".equalsIgnoreCase(failureReason)) {

            score += 0.30;
            signals.add("TRANSIENT_TIMEOUT");

        } else if ("NETWORK_ERROR".equalsIgnoreCase(failureReason)) {

            score += 0.28;
            signals.add("TRANSIENT_NETWORK_ERROR");

        } else if ("BANK_DECLINED".equalsIgnoreCase(failureReason)) {

            score += 0.12;
            signals.add("BANK_DECLINE");

        } else if ("INSUFFICIENT_FUNDS".equalsIgnoreCase(failureReason)) {

            score += 0.08;
            signals.add("INSUFFICIENT_FUNDS");

        } else {

            score += 0.10;
            signals.add("UNKNOWN_FAILURE_REASON");
        }

        /*
         * 2. CUSTOMER HISTORY
         *
         * A customer who historically completes payments is
         * more promising for recovery than a customer with
         * repeated failures.
         */

        if (customerAttempts == 0) {

            score += 0.10;
            signals.add("NEW_CUSTOMER");

        } else if (customerSuccessRate >= 80.0) {

            score += 0.25;
            signals.add("STRONG_CUSTOMER_PAYMENT_HISTORY");

        } else if (customerSuccessRate >= 60.0) {

            score += 0.15;
            signals.add("MODERATE_CUSTOMER_PAYMENT_HISTORY");

        } else if (customerSuccessRate >= 40.0) {

            score += 0.05;
            signals.add("WEAK_CUSTOMER_PAYMENT_HISTORY");

        } else {

            signals.add("POOR_CUSTOMER_PAYMENT_HISTORY");
        }

        /*
         * 3. PAYMENT METHOD HISTORY
         */

        if (methodAttempts >= 5) {

            if (paymentMethodSuccessRate >= 80.0) {

                score += 0.15;
                signals.add("STRONG_PAYMENT_METHOD_HISTORY");

            } else if (paymentMethodSuccessRate >= 60.0) {

                score += 0.08;
                signals.add("MODERATE_PAYMENT_METHOD_HISTORY");

            } else {

                signals.add("WEAK_PAYMENT_METHOD_HISTORY");
            }
        }

        /*
         * 4. RETRY HISTORY
         */

        int retryCount =
                target.getRetryCount() == null
                        ? 0
                        : target.getRetryCount();

        if (retryCount == 0) {

            score += 0.12;
            signals.add("FIRST_RECOVERY_ATTEMPT");

        } else if (retryCount == 1) {

            score += 0.05;
            signals.add("ONE_PREVIOUS_RETRY");

        } else if (retryCount == 2) {

            score -= 0.05;
            signals.add("MULTIPLE_PREVIOUS_RETRIES");

        } else {

            score -= 0.15;
            signals.add("EXCESSIVE_RETRIES");
        }

        /*
         * 5. MERCHANT FAILURE ENVIRONMENT
         *
         * If the merchant itself is experiencing a high failure
         * rate, blindly retrying individual payments is less useful.
         */

        if (merchantFailureRate >= 30.0) {

            score -= 0.10;
            signals.add("HIGH_MERCHANT_FAILURE_RATE");

        } else if (merchantFailureRate >= 20.0) {

            score -= 0.05;
            signals.add("ELEVATED_MERCHANT_FAILURE_RATE");
        }

        /*
         * 6. TRANSACTION AMOUNT
         *
         * Amount does not mean fraud or recovery probability by
         * itself. It affects business priority.
         */

        if (target.getAmount() >= 20000) {

            signals.add("HIGH_VALUE_RECOVERY_OPPORTUNITY");

        } else if (target.getAmount() >= 10000) {

            signals.add("MEDIUM_VALUE_RECOVERY_OPPORTUNITY");

        } else {

            signals.add("LOW_VALUE_RECOVERY_OPPORTUNITY");
        }

        /*
         * ---------------------------------------------------------
         * FINAL SCORE
         * ---------------------------------------------------------
         */

        score = Math.max(
                0.0,
                Math.min(1.0, score)
        );

        double recoveryProbability =
                Math.round(score * 100.0) / 100.0;

        double expectedRecoveryValue =
                Math.round(
                        target.getAmount()
                                * recoveryProbability
                                * 100.0
                ) / 100.0;

        /*
         * ---------------------------------------------------------
         * ACTION
         * ---------------------------------------------------------
         */

        String recommendedAction;

        if (recoveryProbability >= 0.70) {

            recommendedAction = "RETRY";

        } else if (recoveryProbability >= 0.45) {

            recommendedAction = "RETRY_WITH_ALTERNATIVE";

        } else if (recoveryProbability >= 0.30) {

            recommendedAction = "CUSTOMER_REMINDER";

        } else {

            recommendedAction = "DO_NOT_RETRY";
        }

        /*
         * ---------------------------------------------------------
         * REASON
         * ---------------------------------------------------------
         */

        String reason;

        if (recommendedAction.equals("RETRY")) {

            reason =
                    "Transaction shows strong recovery potential based "
                            + "on failure characteristics and historical "
                            + "payment behaviour.";

        } else if (
                recommendedAction.equals(
                        "RETRY_WITH_ALTERNATIVE"
                )) {

            reason =
                    "Transaction has moderate recovery potential; "
                            + "using an alternative payment route may "
                            + "improve recovery.";

        } else if (
                recommendedAction.equals(
                        "CUSTOMER_REMINDER"
                )) {

            reason =
                    "Recovery potential is limited for an immediate "
                            + "retry, but the customer may still complete "
                            + "the payment after a reminder.";

        } else {

            reason =
                    "Historical behaviour and transaction conditions "
                            + "indicate insufficient recovery potential "
                            + "for another automatic attempt.";
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
                "amount",
                target.getAmount()
        );

        result.put(
                "failureReason",
                failureReason
        );

        result.put(
                "retryCount",
                retryCount
        );

        result.put(
                "customerAttempts",
                customerAttempts
        );

        result.put(
                "customerSuccesses",
                customerSuccesses
        );

        result.put(
                "customerFailures",
                customerFailures
        );

        result.put(
                "customerSuccessRate",
                round(customerSuccessRate)
        );

        result.put(
                "paymentMethod",
                target.getPaymentMethod()
        );

        result.put(
                "paymentMethodSuccessRate",
                round(paymentMethodSuccessRate)
        );

        result.put(
                "merchantFailureRate",
                round(merchantFailureRate)
        );

        result.put(
                "recoveryScore",
                recoveryProbability
        );

        result.put(
                "recoveryProbability",
                recoveryProbability
        );

        result.put(
                "expectedRecoveryValue",
                expectedRecoveryValue
        );

        result.put(
                "recommendedAction",
                recommendedAction
        );

        result.put(
                "reason",
                reason
        );

        result.put(
                "signals",
                signals
        );

        return result;
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}