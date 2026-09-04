package com.merchant.intelligence.transaction;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction create(Transaction transaction) {

        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }

        if (transaction.getRetryCount() == null) {
            transaction.setRetryCount(0);
        }

        return repository.save(transaction);
    }

    public List<Transaction> getAll() {
        return repository.findAll();
    }

    public List<Transaction> getFailedTransactions() {
        return repository.findByStatus("FAILED");
    }
}