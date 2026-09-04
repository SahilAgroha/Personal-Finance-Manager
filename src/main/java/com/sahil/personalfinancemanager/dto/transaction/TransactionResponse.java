package com.sahil.personalfinancemanager.dto.transaction;

import com.sahil.personalfinancemanager.entity.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDate date,
        String category,
        String description,
        CategoryType type
) {
}