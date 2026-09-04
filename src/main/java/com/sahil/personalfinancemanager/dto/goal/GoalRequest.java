package com.sahil.personalfinancemanager.dto.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(

        @NotBlank(message = "Goal name is required")
        String goalName,

        @NotNull(message = "Target amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Target amount must be greater than zero"
        )
        BigDecimal targetAmount,

        @NotNull(message = "Target date is required")
        LocalDate targetDate,

        LocalDate startDate
) {
}