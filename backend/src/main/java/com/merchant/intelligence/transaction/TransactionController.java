package com.merchant.intelligence.transaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(
            @RequestBody Transaction transaction) {

        return ResponseEntity.ok(service.create(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Transaction>> getFailed() {
        return ResponseEntity.ok(service.getFailedTransactions());
    }
}