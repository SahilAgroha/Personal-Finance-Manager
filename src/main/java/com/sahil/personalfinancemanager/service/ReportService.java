package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.report.CategoryReportResponse;
import com.sahil.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.sahil.personalfinancemanager.dto.report.ReportResponse;
import com.sahil.personalfinancemanager.dto.report.YearlyReportResponse;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.entity.Transaction;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.repository.TransactionRepository;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ReportService(
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // OVERALL REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public ReportResponse getOverallReport() {

        User user = getCurrentUser();

        List<Transaction> transactions =
                transactionRepository.findAllByUserId(user.getId());

        BigDecimal totalIncome =
                calculateTotal(transactions, CategoryType.INCOME);

        BigDecimal totalExpense =
                calculateTotal(transactions, CategoryType.EXPENSE);

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        return new ReportResponse(
                totalIncome,
                totalExpense,
                balance
        );
    }


    // =========================================================
    // DATE RANGE REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public ReportResponse getDateRangeReport(
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        User user = getCurrentUser();

        List<Transaction> transactions =
                transactionRepository.findByUserIdAndDateBetween(
                        user.getId(),
                        startDate,
                        endDate
                );

        BigDecimal totalIncome =
                calculateTotal(transactions, CategoryType.INCOME);

        BigDecimal totalExpense =
                calculateTotal(transactions, CategoryType.EXPENSE);

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        return new ReportResponse(
                totalIncome,
                totalExpense,
                balance
        );
    }


    // =========================================================
    // CATEGORY-WISE REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public List<CategoryReportResponse> getCategoryReport() {

        User user = getCurrentUser();

        List<Transaction> transactions =
                transactionRepository.findAllByUserId(user.getId());

        Map<String, BigDecimal> categoryTotals =
                transactions.stream()
                        .collect(Collectors.groupingBy(
                                transaction ->
                                        transaction.getCategory().getName(),

                                LinkedHashMap::new,

                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getAmount,
                                        BigDecimal::add
                                )
                        ));

        return categoryTotals.entrySet()
                .stream()
                .map(entry ->
                        new CategoryReportResponse(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }


    // =========================================================
    // EXPENSE CATEGORY-WISE REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public List<CategoryReportResponse> getExpenseCategoryReport() {

        User user = getCurrentUser();

        List<Transaction> transactions =
                transactionRepository.findAllByUserId(user.getId());

        Map<String, BigDecimal> categoryTotals =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getCategory().getType()
                                        == CategoryType.EXPENSE
                        )
                        .collect(Collectors.groupingBy(
                                transaction ->
                                        transaction.getCategory().getName(),

                                LinkedHashMap::new,

                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getAmount,
                                        BigDecimal::add
                                )
                        ));

        return categoryTotals.entrySet()
                .stream()
                .map(entry ->
                        new CategoryReportResponse(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }


    // =========================================================
    // INCOME CATEGORY-WISE REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public List<CategoryReportResponse> getIncomeCategoryReport() {

        User user = getCurrentUser();

        List<Transaction> transactions =
                transactionRepository.findAllByUserId(user.getId());

        Map<String, BigDecimal> categoryTotals =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getCategory().getType()
                                        == CategoryType.INCOME
                        )
                        .collect(Collectors.groupingBy(
                                transaction ->
                                        transaction.getCategory().getName(),

                                LinkedHashMap::new,

                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getAmount,
                                        BigDecimal::add
                                )
                        ));

        return categoryTotals.entrySet()
                .stream()
                .map(entry ->
                        new CategoryReportResponse(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }


    // =========================================================
    // MONTHLY REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(
            int year,
            int month
    ) {

        validateMonth(month);

        User user = getCurrentUser();

        YearMonth yearMonth =
                YearMonth.of(year, month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        List<Transaction> transactions =
                transactionRepository.findByUserIdAndDateBetween(
                        user.getId(),
                        startDate,
                        endDate
                );

        Map<String, BigDecimal> totalIncome =
                calculateCategoryTotals(
                        transactions,
                        CategoryType.INCOME
                );

        Map<String, BigDecimal> totalExpenses =
                calculateCategoryTotals(
                        transactions,
                        CategoryType.EXPENSE
                );

        BigDecimal income =
                calculateTotal(
                        transactions,
                        CategoryType.INCOME
                );

        BigDecimal expenses =
                calculateTotal(
                        transactions,
                        CategoryType.EXPENSE
                );

        BigDecimal netSavings =
                income.subtract(expenses);

        return new MonthlyReportResponse(
                month,
                year,
                totalIncome,
                totalExpenses,
                netSavings
        );
    }


    // =========================================================
    // YEARLY REPORT
    // =========================================================

    @Transactional(readOnly = true)
    public YearlyReportResponse getYearlyReport(
            int year
    ) {

        User user = getCurrentUser();

        LocalDate startDate =
                LocalDate.of(year, 1, 1);

        LocalDate endDate =
                LocalDate.of(year, 12, 31);

        List<Transaction> transactions =
                transactionRepository.findByUserIdAndDateBetween(
                        user.getId(),
                        startDate,
                        endDate
                );

        Map<String, BigDecimal> totalIncome =
                calculateCategoryTotals(
                        transactions,
                        CategoryType.INCOME
                );

        Map<String, BigDecimal> totalExpenses =
                calculateCategoryTotals(
                        transactions,
                        CategoryType.EXPENSE
                );

        BigDecimal income =
                calculateTotal(
                        transactions,
                        CategoryType.INCOME
                );

        BigDecimal expenses =
                calculateTotal(
                        transactions,
                        CategoryType.EXPENSE
                );

        BigDecimal netSavings =
                income.subtract(expenses);

        return new YearlyReportResponse(
                year,
                totalIncome,
                totalExpenses,
                netSavings
        );
    }


    // =========================================================
    // CATEGORY TOTALS HELPER
    // =========================================================

    private Map<String, BigDecimal> calculateCategoryTotals(
            List<Transaction> transactions,
            CategoryType type
    ) {

        return transactions.stream()
                .filter(transaction ->
                        transaction.getCategory().getType()
                                == type
                )
                .collect(Collectors.groupingBy(
                        transaction ->
                                transaction.getCategory().getName(),

                        LinkedHashMap::new,

                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));
    }


    // =========================================================
    // CALCULATE TOTAL
    // =========================================================

    private BigDecimal calculateTotal(
            List<Transaction> transactions,
            CategoryType type
    ) {

        return transactions.stream()
                .filter(transaction ->
                        transaction.getCategory().getType()
                                == type
                )
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }


    // =========================================================
    // VALIDATE DATE RANGE
    // =========================================================

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {

            throw new BadRequestException(
                    "Both startDate and endDate are required"
            );
        }

        if (startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date"
            );
        }
    }


    // =========================================================
    // VALIDATE MONTH
    // =========================================================

    private void validateMonth(int month) {

        if (month < 1 || month > 12) {

            throw new BadRequestException(
                    "Month must be between 1 and 12"
            );
        }
    }


    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BadRequestException(
                    "User is not authenticated"
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Authenticated user not found"
                        )
                );
    }
}