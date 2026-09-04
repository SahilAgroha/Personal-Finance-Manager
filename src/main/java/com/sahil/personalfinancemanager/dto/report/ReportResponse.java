package com.sahil.personalfinancemanager.dto.report;

import java.math.BigDecimal;

public record ReportResponse(

        BigDecimal totalIncome,

        BigDecimal totalExpense,

        BigDecimal balance

) {
}