package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.report.CategoryReportResponse;
import com.sahil.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.sahil.personalfinancemanager.dto.report.ReportResponse;
import com.sahil.personalfinancemanager.dto.report.YearlyReportResponse;
import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.entity.Transaction;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.repository.TransactionRepository;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;
    private Transaction incomeTransaction;
    private Transaction expenseTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "password", "Test User", "1234567890");
        testUser.setId(1L);

        Category incomeCategory = new Category("Salary", CategoryType.INCOME, false, null);
        Category expenseCategory = new Category("Food", CategoryType.EXPENSE, false, null);

        incomeTransaction = new Transaction(new BigDecimal("5000.00"), LocalDate.now(), "Salary", incomeCategory, testUser);
        expenseTransaction = new Transaction(new BigDecimal("1000.00"), LocalDate.now(), "Groceries", expenseCategory, testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findByUsername("test@test.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getOverallReport_Success() {
        mockAuthentication();
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(incomeTransaction, expenseTransaction));

        ReportResponse response = reportService.getOverallReport();

        assertEquals(0, new BigDecimal("5000.00").compareTo(response.totalIncome()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(response.totalExpense()));
        assertEquals(0, new BigDecimal("4000.00").compareTo(response.balance()));
    }

    @Test
    void getDateRangeReport_Success() {
        mockAuthentication();
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        when(transactionRepository.findByUserIdAndDateBetween(1L, startDate, endDate))
                .thenReturn(List.of(incomeTransaction, expenseTransaction));

        ReportResponse response = reportService.getDateRangeReport(startDate, endDate);

        assertEquals(0, new BigDecimal("5000.00").compareTo(response.totalIncome()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(response.totalExpense()));
        assertEquals(0, new BigDecimal("4000.00").compareTo(response.balance()));
    }

    @Test
    void getDateRangeReport_NullDates_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> reportService.getDateRangeReport(null, LocalDate.now()));
        assertThrows(BadRequestException.class, () -> reportService.getDateRangeReport(LocalDate.now(), null));
    }

    @Test
    void getDateRangeReport_StartDateAfterEndDate_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> reportService.getDateRangeReport(LocalDate.now().plusDays(1), LocalDate.now()));
    }

    @Test
    void getCategoryReport_Success() {
        mockAuthentication();
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(incomeTransaction, expenseTransaction));

        List<CategoryReportResponse> response = reportService.getCategoryReport();

        assertEquals(2, response.size());
    }

    @Test
    void getExpenseCategoryReport_Success() {
        mockAuthentication();
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(incomeTransaction, expenseTransaction));

        List<CategoryReportResponse> response = reportService.getExpenseCategoryReport();

        assertEquals(1, response.size());
        assertEquals("Food", response.get(0).category());
        assertEquals(0, new BigDecimal("1000.00").compareTo(response.get(0).amount()));
    }

    @Test
    void getIncomeCategoryReport_Success() {
        mockAuthentication();
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(incomeTransaction, expenseTransaction));

        List<CategoryReportResponse> response = reportService.getIncomeCategoryReport();

        assertEquals(1, response.size());
        assertEquals("Salary", response.get(0).category());
        assertEquals(0, new BigDecimal("5000.00").compareTo(response.get(0).amount()));
    }

    @Test
    void getMonthlyReport_Success() {
        mockAuthentication();
        when(transactionRepository.findByUserIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(incomeTransaction, expenseTransaction));

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 1);

        assertEquals(1, response.month());
        assertEquals(2024, response.year());
        assertEquals(0, new BigDecimal("4000.00").compareTo(response.netSavings()));
    }

    @Test
    void getMonthlyReport_InvalidMonth_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> reportService.getMonthlyReport(2024, 0));
        assertThrows(BadRequestException.class, () -> reportService.getMonthlyReport(2024, 13));
    }

    @Test
    void getYearlyReport_Success() {
        mockAuthentication();
        when(transactionRepository.findByUserIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(incomeTransaction, expenseTransaction));

        YearlyReportResponse response = reportService.getYearlyReport(2024);

        assertEquals(2024, response.year());
        assertEquals(0, new BigDecimal("4000.00").compareTo(response.netSavings()));
    }

    @Test
    void getCurrentUser_NotAuthenticated_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> reportService.getOverallReport());
    }
    
    @Test
    void getCurrentUser_UserNotFound_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("notfound@test.com");
        when(userRepository.findByUsername("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> reportService.getOverallReport());
    }

    @Test
    void getCurrentUser_NullAuthentication_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThrows(
                BadRequestException.class,
                () -> reportService.getOverallReport()
        );
    }
}
