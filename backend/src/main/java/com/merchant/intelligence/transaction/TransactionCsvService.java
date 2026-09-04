package com.merchant.intelligence.transaction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionCsvService {

    private final TransactionRepository transactionRepository;

    public TransactionCsvService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public int importTransactions(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty.");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported.");
        }

        List<Transaction> transactions = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreEmptyLines(true)
                        .build()
                        .parse(reader)
        ) {

            for (CSVRecord record : parser) {

                Transaction transaction = new Transaction();

                transaction.setTransactionId(
                        record.get("transaction_id")
                );

                transaction.setMerchantId(
                        record.get("merchant_id")
                );

                transaction.setCustomerId(
                        record.get("customer_id")
                );

                transaction.setAmount(
                        Double.parseDouble(
                                record.get("amount")
                        )
                );

                transaction.setPaymentMethod(
                        record.get("payment_method")
                );

                transaction.setStatus(
                        record.get("status")
                );

                String failureReason = record.get("failure_reason");

                if (failureReason != null && !failureReason.isBlank()) {
                    transaction.setFailureReason(failureReason);
                }

                transaction.setRetryCount(
                        Integer.parseInt(
                                record.get("retry_count")
                        )
                );

                transaction.setCreatedAt(
                        LocalDateTime.parse(
                                record.get("created_at")
                        )
                );

                transactions.add(transaction);
            }
        }

        transactionRepository.saveAll(transactions);

        return transactions.size();
    }
}