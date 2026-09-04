package com.sahil.personalfinancemanager.dto.goal;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalUpdateRequest(

        String goalName,

        @DecimalMin(
                value = "0.01",
                message = "Target amount must be greater than zero"
        )
        BigDecimal targetAmount,

        LocalDate targetDate
) {
}