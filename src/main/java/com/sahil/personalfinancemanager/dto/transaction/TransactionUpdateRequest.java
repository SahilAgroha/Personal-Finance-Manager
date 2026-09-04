package com.sahil.personalfinancemanager.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import java.time.LocalDate;

public record TransactionUpdateRequest(

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        LocalDate date,

        String category,

        String description
) {
}
