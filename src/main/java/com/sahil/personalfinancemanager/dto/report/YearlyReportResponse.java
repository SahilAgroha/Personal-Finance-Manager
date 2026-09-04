package com.sahil.personalfinancemanager.dto.report;

import java.math.BigDecimal;
import java.util.Map;

public record YearlyReportResponse(

        Integer year,

        Map<String, BigDecimal> totalIncome,

        Map<String, BigDecimal> totalExpenses,

        BigDecimal netSavings

) {
}