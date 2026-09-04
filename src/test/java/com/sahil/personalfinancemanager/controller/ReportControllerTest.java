package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.report.CategoryReportResponse;
import com.sahil.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.sahil.personalfinancemanager.dto.report.ReportResponse;
import com.sahil.personalfinancemanager.dto.report.YearlyReportResponse;
import com.sahil.personalfinancemanager.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    @Test
    void getOverallReport_Success() throws Exception {
        ReportResponse response = new ReportResponse(new BigDecimal("5000.00"), new BigDecimal("1000.00"), new BigDecimal("4000.00"));
        when(reportService.getOverallReport()).thenReturn(response);

        mockMvc.perform(get("/api/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(4000.00));
    }

    @Test
    void getDateRangeReport_Success() throws Exception {
        ReportResponse response = new ReportResponse(new BigDecimal("5000.00"), new BigDecimal("1000.00"), new BigDecimal("4000.00"));
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        when(reportService.getDateRangeReport(startDate, endDate)).thenReturn(response);

        mockMvc.perform(get("/api/reports/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(4000.00));
    }

    @Test
    void getCategoryReport_Success() throws Exception {
        CategoryReportResponse response = new CategoryReportResponse("Food", new BigDecimal("1000.00"));
        when(reportService.getCategoryReport()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/reports/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].category").value("Food"));
    }

    @Test
    void getExpenseCategoryReport_Success() throws Exception {
        CategoryReportResponse response = new CategoryReportResponse("Food", new BigDecimal("1000.00"));
        when(reportService.getExpenseCategoryReport()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/reports/categories/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].category").value("Food"));
    }

    @Test
    void getIncomeCategoryReport_Success() throws Exception {
        CategoryReportResponse response = new CategoryReportResponse("Salary", new BigDecimal("5000.00"));
        when(reportService.getIncomeCategoryReport()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/reports/categories/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].category").value("Salary"));
    }

    @Test
    void getMonthlyReport_Success() throws Exception {
        MonthlyReportResponse response = new MonthlyReportResponse(1, 2024, Map.of(), Map.of(), new BigDecimal("4000.00"));
        when(reportService.getMonthlyReport(2024, 1)).thenReturn(response);

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netSavings").value(4000.00));
    }

    @Test
    void getYearlyReport_Success() throws Exception {
        YearlyReportResponse response = new YearlyReportResponse(2024, Map.of(), Map.of(), new BigDecimal("4000.00"));
        when(reportService.getYearlyReport(2024)).thenReturn(response);

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netSavings").value(4000.00));
    }
}
