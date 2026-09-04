package com.sahil.personalfinancemanager.dto.report;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyReportResponse(

        Integer month,

        Integer year,

        Map<String, BigDecimal> totalIncome,

        Map<String, BigDecimal> totalExpenses,

        BigDecimal netSavings

) {
}