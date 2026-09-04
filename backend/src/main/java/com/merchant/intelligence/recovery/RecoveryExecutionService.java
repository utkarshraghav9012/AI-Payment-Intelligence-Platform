package com.merchant.intelligence.recovery;

import com.merchant.intelligence.transaction.Transaction;
import com.merchant.intelligence.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RecoveryExecutionService {

    private final TransactionRepository transactionRepository;
    private final RecoveryAttemptRepository recoveryAttemptRepository;
    private final RecoveryScoringService recoveryScoringService;

    private final Random random = new Random();

    public RecoveryExecutionService(
            TransactionRepository transactionRepository,
            RecoveryAttemptRepository recoveryAttemptRepository,
            RecoveryScoringService recoveryScoringService) {

        this.transactionRepository = transactionRepository;
        this.recoveryAttemptRepository = recoveryAttemptRepository;
        this.recoveryScoringService = recoveryScoringService;
    }

    public Map<String, Object> executeRecovery(
            String transactionId) {

        Transaction transaction = transactionRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found: " + transactionId
                        ));

        if (!"FAILED".equalsIgnoreCase(transaction.getStatus())) {
            throw new IllegalStateException(
                    "Recovery can only be attempted for failed transactions."
            );
        }

        Map<String, Object> scoreResult =
                recoveryScoringService.calculateRecoveryScore(
                        transactionId
                );

        double recoveryScore =
                ((Number) scoreResult.get("recoveryScore"))
                        .doubleValue();

        String recommendedAction =
                (String) scoreResult.get("recommendedAction");

        /*
         * Safety gate:
         * Never execute an automatic recovery when
         * the decision engine says DO_NOT_RETRY.
         */
        if ("DO_NOT_RETRY".equalsIgnoreCase(recommendedAction)) {

            Map<String, Object> response = new HashMap<>();

            response.put(
                    "transactionId",
                    transaction.getTransactionId()
            );

            response.put(
                    "amount",
                    transaction.getAmount()
            );

            response.put(
                    "recoveryScore",
                    recoveryScore
            );

            response.put(
                    "recommendedAction",
                    recommendedAction
            );

            response.put(
                    "recoveryAttempted",
                    false
            );

            response.put(
                    "recoverySuccessful",
                    false
            );

            response.put(
                    "status",
                    transaction.getStatus()
            );

            response.put(
                    "retryCount",
                    transaction.getRetryCount()
            );

            response.put(
                    "recoveredRevenue",
                    0.0
            );

            response.put(
                    "message",
                    "Recovery not executed because the transaction "
                            + "was classified as low recovery potential."
            );

            return response;
        }

        String failureReason = transaction.getFailureReason();

        boolean recoverySuccessful;

        if ("RETRY".equalsIgnoreCase(recommendedAction)) {

    if ("TIMEOUT".equalsIgnoreCase(failureReason)) {

        recoverySuccessful = random.nextDouble() < 0.70;

    } else if ("NETWORK_ERROR".equalsIgnoreCase(failureReason)) {

        recoverySuccessful = random.nextDouble() < 0.70;

    } else {

        recoverySuccessful = random.nextDouble() < 0.40;
    }

} else if ("RETRY_WITH_ALTERNATIVE".equalsIgnoreCase(recommendedAction)) {

    if ("TIMEOUT".equalsIgnoreCase(failureReason)) {

        recoverySuccessful = random.nextDouble() < 0.55;

    } else if ("NETWORK_ERROR".equalsIgnoreCase(failureReason)) {

        recoverySuccessful = random.nextDouble() < 0.55;

    } else {

        recoverySuccessful = random.nextDouble() < 0.30;
    }

} else {

    recoverySuccessful = false;
}

        transaction.setRetryCount(
                transaction.getRetryCount() + 1
        );

        String result;
        double recoveredRevenue;

        if (recoverySuccessful) {

            transaction.setStatus("SUCCESS");
            transaction.setFailureReason(null);

            result = "SUCCESS";
            recoveredRevenue = transaction.getAmount();

        } else {

            result = "FAILED";
            recoveredRevenue = 0.0;
        }

        transactionRepository.save(transaction);

        RecoveryAttempt attempt = new RecoveryAttempt();

        attempt.setTransactionId(
                transaction.getTransactionId()
        );

        attempt.setAction(
                recommendedAction
        );

        attempt.setRecoveryScore(
                recoveryScore
        );

        attempt.setResult(
                result
        );

        attempt.setRecoveredRevenue(
                recoveredRevenue
        );

        attempt.setAttemptedAt(
                LocalDateTime.now()
        );

        recoveryAttemptRepository.save(attempt);

        Map<String, Object> response = new HashMap<>();

        response.put(
                "transactionId",
                transaction.getTransactionId()
        );

        response.put(
                "amount",
                transaction.getAmount()
        );

        response.put(
                "recoveryScore",
                recoveryScore
        );

        response.put(
                "recommendedAction",
                recommendedAction
        );

        response.put(
                "recoveryAttempted",
                true
        );

        response.put(
                "recoverySuccessful",
                recoverySuccessful
        );

        response.put(
                "status",
                transaction.getStatus()
        );

        response.put(
                "retryCount",
                transaction.getRetryCount()
        );

        response.put(
                "recoveredRevenue",
                recoveredRevenue
        );

        response.put(
                "message",
                recoverySuccessful
                        ? "Recovery simulation succeeded."
                        : "Recovery simulation failed."
        );

        return response;
    }
}