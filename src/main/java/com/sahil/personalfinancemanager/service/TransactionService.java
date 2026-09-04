package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.transaction.TransactionRequest;
import com.sahil.personalfinancemanager.dto.transaction.TransactionResponse;
import com.sahil.personalfinancemanager.dto.transaction.TransactionUpdateRequest;
import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.Transaction;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.exception.ResourceNotFoundException;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import com.sahil.personalfinancemanager.repository.TransactionRepository;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE TRANSACTION
    // =========================================================

    @Transactional
    public TransactionResponse createTransaction(
            TransactionRequest request
    ) {

        // Validate transaction date
        validateDate(request.date());

        // Get authenticated user
        User user = getCurrentUser();

        // Find default or user's custom category
        Category category = findAccessibleCategory(
                request.category(),
                user
        );

        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setCategory(category);
        transaction.setDescription(request.description());
        transaction.setUser(user);

        // Save
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return toResponse(savedTransaction);
    }

    // =========================================================
    // GET ALL / FILTER TRANSACTIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(
            LocalDate startDate,
            LocalDate endDate,
            String category
    ) {

        validateDateRange(startDate, endDate);

        User user = getCurrentUser();

        List<Transaction> transactions;

        // No filters
        if (startDate == null && category == null) {

            transactions =
                    transactionRepository.findAllByUserId(
                            user.getId()
                    );
        }

        // Date range + category
        else if (startDate != null
                && category != null
                && !category.isBlank()) {

            transactions =
                    transactionRepository
                            .findByUserIdAndCategoryAndDateBetween(
                                    user.getId(),
                                    category,
                                    startDate,
                                    endDate
                            );
        }

        // Date range
        else if (startDate != null) {

            transactions =
                    transactionRepository
                            .findByUserIdAndDateBetween(
                                    user.getId(),
                                    startDate,
                                    endDate
                            );
        }

        // Category only
        else if (category != null
                && !category.isBlank()) {

            transactions =
                    transactionRepository
                            .findByUserIdAndCategory(
                                    user.getId(),
                                    category
                            );
        }

        // Invalid filter
        else {

            throw new BadRequestException(
                    "Both startDate and endDate are required for date filtering"
            );
        }

        return transactions.stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // GET TRANSACTION BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(
            Long transactionId
    ) {

        User user = getCurrentUser();

        Transaction transaction =
                transactionRepository
                        .findByIdAndUserId(
                                transactionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        return toResponse(transaction);
    }

    // =========================================================
    // UPDATE TRANSACTION
    // =========================================================

    @Transactional
    public TransactionResponse updateTransaction(
            Long transactionId,
            TransactionUpdateRequest request
    ) {

        User user = getCurrentUser();

        // Find transaction belonging to current user
        Transaction transaction =
                transactionRepository
                        .findByIdAndUserId(
                                transactionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        // -----------------------------------------------------
        // Update amount
        // -----------------------------------------------------

        if (request.amount() != null) {

            if (request.amount().signum() <= 0) {
                throw new BadRequestException(
                        "Amount must be greater than zero"
                );
            }

            transaction.setAmount(
                    request.amount()
            );
        }

        // -----------------------------------------------------
        // Update category
        // -----------------------------------------------------

        if (request.category() != null
                && !request.category().isBlank()) {

            Category category =
                    findAccessibleCategory(
                            request.category(),
                            user
                    );

            transaction.setCategory(category);
        }

        // -----------------------------------------------------
        // Update description
        // -----------------------------------------------------

        if (request.description() != null) {

            transaction.setDescription(
                    request.description()
            );
        }

        /*
         * IMPORTANT:
         *
         * request.date() is intentionally ignored.
         *
         * Transaction date cannot be changed.
         *
         * Example:
         *
         * Existing:
         * date = 2024-01-15
         *
         * Request:
         * {
         *     "date": "2024-01-20"
         * }
         *
         * Result:
         * date = 2024-01-15
         */

        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        return toResponse(updatedTransaction);
    }

    // =========================================================
    // DELETE TRANSACTION
    // =========================================================

    @Transactional
    public void deleteTransaction(
            Long transactionId
    ) {

        User user = getCurrentUser();

        // Find only if transaction belongs to current user
        Transaction transaction =
                transactionRepository
                        .findByIdAndUserId(
                                transactionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        transactionRepository.delete(transaction);
    }

    // =========================================================
    // FIND ACCESSIBLE CATEGORY
    // =========================================================

    private Category findAccessibleCategory(
            String categoryName,
            User user
    ) {

        // -----------------------------------------------------
        // Check default category
        // -----------------------------------------------------

        Category category =
                categoryRepository
                        .findByNameAndCustomCategoryFalse(
                                categoryName
                        )
                        .orElse(null);

        if (category != null) {
            return category;
        }

        // -----------------------------------------------------
        // Check user's custom category
        // -----------------------------------------------------

        return categoryRepository
                .findByNameAndUserId(
                        categoryName,
                        user.getId()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid category: "
                                        + categoryName
                        )
                );
    }

    // =========================================================
    // VALIDATE TRANSACTION DATE
    // =========================================================

    private void validateDate(
            LocalDate date
    ) {

        if (date == null) {

            throw new BadRequestException(
                    "Date is required"
            );
        }

        if (date.isAfter(LocalDate.now())) {

            throw new BadRequestException(
                    "Transaction date cannot be in the future"
            );
        }
    }

    // =========================================================
    // VALIDATE DATE RANGE
    // =========================================================

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Only one date supplied
        if ((startDate == null && endDate != null)
                || (startDate != null && endDate == null)) {

            throw new BadRequestException(
                    "Both startDate and endDate are required"
            );
        }

        // Start date cannot be after end date
        if (startDate != null
                && endDate != null
                && startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date"
            );
        }
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BadRequestException(
                    "User is not authenticated"
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private TransactionResponse toResponse(
            Transaction transaction
    ) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getCategory().getType()
        );
    }
}