package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecoveryDecisionService {

    private final TransactionRepository transactionRepository;
    private final RecoveryProbabilityService recoveryProbabilityService;
    private final RiskSignalService riskSignalService;
    private final RecoveryScoringService recoveryScoringService;

    public RecoveryDecisionService(
            TransactionRepository transactionRepository,
            RecoveryProbabilityService recoveryProbabilityService,
            RiskSignalService riskSignalService,
            RecoveryScoringService recoveryScoringService) {

        this.transactionRepository = transactionRepository;
        this.recoveryProbabilityService = recoveryProbabilityService;
        this.riskSignalService = riskSignalService;
        this.recoveryScoringService = recoveryScoringService;
    }

    public Map<String, Object> getDecision(String transactionId) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Transaction not found: "
                                                + transactionId
                                ));

        /*
         * ---------------------------------------------------------
         * SUCCESSFUL TRANSACTION GUARD
         * ---------------------------------------------------------
         *
         * Recovery intelligence is only applicable to failed
         * transactions.
         *
         * Once a payment has been successfully recovered,
         * no further recovery action should be recommended.
         */

        if (!"FAILED".equalsIgnoreCase(transaction.getStatus())) {

            Map<String, Object> result =
                    new LinkedHashMap<>();

            result.put(
                    "transactionId",
                    transaction.getTransactionId()
            );

            result.put(
                    "merchantId",
                    transaction.getMerchantId()
            );

            result.put(
                    "customerId",
                    transaction.getCustomerId()
            );

            result.put(
                    "amount",
                    transaction.getAmount()
            );

            result.put(
                    "failureReason",
                    transaction.getFailureReason()
            );

            result.put(
                    "retryCount",
                    transaction.getRetryCount()
            );

            result.put(
                    "riskScore",
                    0
            );

            result.put(
                    "riskLevel",
                    "LOW"
            );

            result.put(
                    "recoveryProbability",
                    0.0
            );

            result.put(
                    "expectedRecoveryValue",
                    0.0
            );

            result.put(
                    "recommendedAction",
                    "NO_ACTION"
            );

            result.put(
                    "decisionReason",
                    "Payment already completed successfully; "
                            + "no recovery action required."
            );

            result.put(
                    "decisionConfidence",
                    "HIGH"
            );

            result.put(
                    "recoverySignals",
                    List.of()
            );

            result.put(
                    "riskSignals",
                    List.of()
            );

            return result;
        }

        /*
         * ---------------------------------------------------------
         * RECOVERY INTELLIGENCE
         * ---------------------------------------------------------
         */

        Map<String, Object> recovery =
                recoveryScoringService.calculateRecoveryScore(
                        transactionId
                );

        double recoveryProbability =
                ((Number) recovery.get("recoveryProbability"))
                        .doubleValue();

        double expectedRecoveryValue =
                ((Number) recovery.get("expectedRecoveryValue"))
                        .doubleValue();

        /*
         * ---------------------------------------------------------
         * RISK INTELLIGENCE
         * ---------------------------------------------------------
         */

        Map<String, Object> risk =
                riskSignalService.calculateRiskSignals(
                        transactionId
                );

        String riskLevel =
                String.valueOf(
                        risk.get("riskLevel")
                );

        int riskScore =
                ((Number) risk.get("riskScore"))
                        .intValue();

        /*
         * ---------------------------------------------------------
         * DECISION
         * ---------------------------------------------------------
         */

        String action;
        String decisionReason;
        String decisionConfidence;

        /*
         * ---------------------------------------------------------
         * HIGH RISK
         * ---------------------------------------------------------
         *
         * Never automatically retry a transaction with
         * elevated behavioural risk.
         */

        if ("HIGH".equalsIgnoreCase(riskLevel)) {

            action = "MANUAL_REVIEW";

            decisionReason =
                    "High behavioural risk detected. "
                            + "Automatic recovery is blocked "
                            + "until the transaction is reviewed.";

            decisionConfidence = "HIGH";

        /*
         * ---------------------------------------------------------
         * MEDIUM RISK
         * ---------------------------------------------------------
         */

        } else if ("MEDIUM".equalsIgnoreCase(riskLevel)) {

            if (recoveryProbability >= 0.60
                    && expectedRecoveryValue >= 5000) {

                action = "RETRY_WITH_CAUTION";

                decisionReason =
                        "Moderate behavioural risk but strong "
                                + "recovery potential. A controlled "
                                + "retry is recommended.";

                decisionConfidence = "MEDIUM";

            } else if (recoveryProbability >= 0.30) {

                action = "CUSTOMER_REMINDER";

                decisionReason =
                        "Moderate behavioural risk makes an immediate "
                                + "automatic retry less suitable, but "
                                + "the customer still has measurable "
                                + "payment recovery potential.";

                decisionConfidence = "MEDIUM";

            } else {

                action = "MANUAL_REVIEW";

                decisionReason =
                        "Moderate risk combined with limited recovery "
                                + "potential. Automatic recovery is "
                                + "not recommended.";

                decisionConfidence = "MEDIUM";
            }

        /*
         * ---------------------------------------------------------
         * LOW RISK
         * ---------------------------------------------------------
         */

        } else {

            if (recoveryProbability >= 0.70
                    && expectedRecoveryValue >= 5000) {

                action = "RETRY";

                decisionReason =
                        "Low behavioural risk and strong recovery "
                                + "potential. Automatic retry is "
                                + "recommended.";

                decisionConfidence = "HIGH";

            } else if (recoveryProbability >= 0.45
                    && expectedRecoveryValue >= 2000) {

                action = "RETRY_WITH_ALTERNATIVE";

                decisionReason =
                        "Low behavioural risk with moderate recovery "
                                + "potential. An alternative payment "
                                + "route should be considered.";

                decisionConfidence = "MEDIUM";

            } else if (recoveryProbability >= 0.30) {

                action = "CUSTOMER_REMINDER";

                decisionReason =
                        "Immediate retry has limited expected value, "
                                + "but the customer still shows enough "
                                + "recovery potential for a payment "
                                + "reminder.";

                decisionConfidence = "MEDIUM";

            } else {

                action = "DO_NOT_RETRY";

                decisionReason =
                        "Recovery potential is too low to justify "
                                + "another automatic payment attempt.";

                decisionConfidence = "HIGH";
            }
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
                transaction.getTransactionId()
        );

        result.put(
                "merchantId",
                transaction.getMerchantId()
        );

        result.put(
                "customerId",
                transaction.getCustomerId()
        );

        result.put(
                "amount",
                transaction.getAmount()
        );

        result.put(
                "failureReason",
                transaction.getFailureReason()
        );

        result.put(
                "retryCount",
                transaction.getRetryCount()
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
                "recoveryProbability",
                recoveryProbability
        );

        result.put(
                "expectedRecoveryValue",
                expectedRecoveryValue
        );

        result.put(
                "recommendedAction",
                action
        );

        result.put(
                "decisionReason",
                decisionReason
        );

        result.put(
                "decisionConfidence",
                decisionConfidence
        );

        result.put(
                "recoverySignals",
                recovery.get("signals")
        );

        result.put(
                "riskSignals",
                risk.get("signals")
        );

        return result;
    }
}