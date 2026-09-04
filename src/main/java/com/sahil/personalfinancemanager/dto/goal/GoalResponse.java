package com.sahil.personalfinancemanager.dto.goal;

import com.sahil.personalfinancemanager.config.GoalAmountSerializer;
import com.sahil.personalfinancemanager.config.GoalPercentageSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalResponse(

        Long id,

        String goalName,

        BigDecimal targetAmount,

        LocalDate targetDate,

        LocalDate startDate,

        @JsonSerialize(using = GoalAmountSerializer.class)
        BigDecimal currentProgress,

        @JsonSerialize(using = GoalPercentageSerializer.class)
        BigDecimal progressPercentage,

        BigDecimal remainingAmount
) {
}