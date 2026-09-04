package com.merchant.intelligence.recovery;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecoveryPerformanceService {

    private final RecoveryAttemptRepository recoveryAttemptRepository;

    public RecoveryPerformanceService(
            RecoveryAttemptRepository recoveryAttemptRepository) {

        this.recoveryAttemptRepository = recoveryAttemptRepository;
    }

    public Map<String, Object> getRecoveryPerformance() {

        List<RecoveryAttempt> attempts =
                recoveryAttemptRepository.findAll();

        long totalAttempts = attempts.size();

        long successfulAttempts = attempts.stream()
                .filter(attempt ->
                        "SUCCESS".equalsIgnoreCase(attempt.getResult()))
                .count();

        long failedAttempts = attempts.stream()
                .filter(attempt ->
                        "FAILED".equalsIgnoreCase(attempt.getResult()))
                .count();

        double recoveredRevenue = attempts.stream()
                .mapToDouble(RecoveryAttempt::getRecoveredRevenue)
                .sum();

        double recoveryRate = totalAttempts == 0
                ? 0.0
                : (successfulAttempts * 100.0) / totalAttempts;

        Map<String, Object> result = new HashMap<>();

        result.put("totalAttempts", totalAttempts);
        result.put("successfulAttempts", successfulAttempts);
        result.put("failedAttempts", failedAttempts);
        result.put("recoveredRevenue", recoveredRevenue);
        result.put(
                "recoveryRate",
                Math.round(recoveryRate * 100.0) / 100.0
        );

        return result;
    }
}