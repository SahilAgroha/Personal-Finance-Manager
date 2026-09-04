package com.sahil.personalfinancemanager.dto.report;

import java.math.BigDecimal;

public record CategoryReportResponse(

        String category,

        BigDecimal amount

) {
}