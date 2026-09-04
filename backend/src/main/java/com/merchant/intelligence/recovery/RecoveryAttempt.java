package com.merchant.intelligence.recovery;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recovery_attempts")
public class RecoveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Double recoveryScore;

    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private Double recoveredRevenue;

    @Column(nullable = false)
    private LocalDateTime attemptedAt;

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getRecoveryScore() {
        return recoveryScore;
    }

    public void setRecoveryScore(Double recoveryScore) {
        this.recoveryScore = recoveryScore;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Double getRecoveredRevenue() {
        return recoveredRevenue;
    }

    public void setRecoveredRevenue(Double recoveredRevenue) {
        this.recoveredRevenue = recoveredRevenue;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}