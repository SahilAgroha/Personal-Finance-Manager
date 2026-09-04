package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.transaction.TransactionRequest;
import com.sahil.personalfinancemanager.dto.transaction.TransactionResponse;
import com.sahil.personalfinancemanager.dto.transaction.TransactionUpdateRequest;
import com.sahil.personalfinancemanager.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @Valid @RequestBody TransactionRequest request
    ) {

        return transactionService.createTransaction(request);
    }

    // =========================================================
    // GET ALL / FILTER
    // =========================================================

    @GetMapping
    public Map<String, List<TransactionResponse>> getTransactions(
            @RequestParam(required = false)
            LocalDate startDate,

            @RequestParam(required = false)
            LocalDate endDate,

            @RequestParam(required = false)
            String category
    ) {

        List<TransactionResponse> transactions =
                transactionService.getTransactions(
                        startDate,
                        endDate,
                        category
                );

        return Map.of(
                "transactions",
                transactions
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public TransactionResponse getTransaction(
            @PathVariable Long id
    ) {

        return transactionService.getTransaction(id);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(
            @PathVariable Long id,

            @Valid
            @RequestBody
            TransactionUpdateRequest request
    ) {

        return transactionService.updateTransaction(
                id,
                request
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    public Map<String, String> deleteTransaction(
            @PathVariable Long id
    ) {

        transactionService.deleteTransaction(id);

        return Map.of(
                "message",
                "Transaction deleted successfully"
        );
    }
}