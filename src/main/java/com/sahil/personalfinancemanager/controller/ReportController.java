package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.report.CategoryReportResponse;
import com.sahil.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.sahil.personalfinancemanager.dto.report.ReportResponse;
import com.sahil.personalfinancemanager.dto.report.YearlyReportResponse;
import com.sahil.personalfinancemanager.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }


    // =========================================================
    // OVERALL REPORT
    // =========================================================

    @GetMapping("/summary")
    public ResponseEntity<ReportResponse> getOverallReport() {

        return ResponseEntity.ok(
                reportService.getOverallReport()
        );
    }


    // =========================================================
    // DATE RANGE REPORT
    // =========================================================

    @GetMapping("/date-range")
    public ResponseEntity<ReportResponse> getDateRangeReport(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate

    ) {

        return ResponseEntity.ok(
                reportService.getDateRangeReport(
                        startDate,
                        endDate
                )
        );
    }


    // =========================================================
    // ALL CATEGORY REPORT
    // =========================================================

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportResponse>>
    getCategoryReport() {

        return ResponseEntity.ok(
                reportService.getCategoryReport()
        );
    }


    // =========================================================
    // EXPENSE CATEGORY REPORT
    // =========================================================

    @GetMapping("/categories/expenses")
    public ResponseEntity<List<CategoryReportResponse>>
    getExpenseCategoryReport() {

        return ResponseEntity.ok(
                reportService.getExpenseCategoryReport()
        );
    }


    // =========================================================
    // INCOME CATEGORY REPORT
    // =========================================================

    @GetMapping("/categories/income")
    public ResponseEntity<List<CategoryReportResponse>>
    getIncomeCategoryReport() {

        return ResponseEntity.ok(
                reportService.getIncomeCategoryReport()
        );
    }


    // =========================================================
    // MONTHLY REPORT
    // =========================================================

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse>
    getMonthlyReport(

            @PathVariable int year,

            @PathVariable int month

    ) {

        return ResponseEntity.ok(
                reportService.getMonthlyReport(
                        year,
                        month
                )
        );
    }


    // =========================================================
    // YEARLY REPORT
    // =========================================================

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse>
    getYearlyReport(

            @PathVariable int year

    ) {

        return ResponseEntity.ok(
                reportService.getYearlyReport(
                        year
                )
        );
    }
}