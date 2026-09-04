package com.merchant.intelligence.transaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionCsvController {

    private final TransactionCsvService csvService;

    public TransactionCsvController(TransactionCsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importTransactions(
            @RequestParam("file") MultipartFile file) {

        try {

            int importedCount = csvService.importTransactions(file);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Transactions imported successfully.",
                            "importedCount", importedCount
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "message", "Failed to import transactions."
                            )
                    );
        }
    }
}