package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.transaction.TransactionRequest;
import com.sahil.personalfinancemanager.dto.transaction.TransactionResponse;
import com.sahil.personalfinancemanager.dto.transaction.TransactionUpdateRequest;
import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.entity.Transaction;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.exception.ResourceNotFoundException;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "password", "Test User", "1234567890");
        testUser.setId(1L);

        testCategory = new Category("Salary", CategoryType.INCOME, false, null);
        testCategory.setId(1L);

        testTransaction = new Transaction(new BigDecimal("5000.00"), LocalDate.now(), "Salary", testCategory, testUser);
        testTransaction.setId(1L);
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
    void createTransaction_Success_DefaultCategory() {
        mockAuthentication();
        TransactionRequest request = new TransactionRequest(new BigDecimal("5000.00"), LocalDate.now(), "Salary", "Salary");
        
        when(categoryRepository.findByNameAndCustomCategoryFalse("Salary")).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals("Salary", response.category());
        verify(transactionRepository).save(any(Transaction.class));
    }
    
    @Test
    void createTransaction_Success_CustomCategory() {
        mockAuthentication();
        TransactionRequest request = new TransactionRequest(new BigDecimal("5000.00"), LocalDate.now(), "MyIncome", "Salary");
        
        when(categoryRepository.findByNameAndCustomCategoryFalse("MyIncome")).thenReturn(Optional.empty());
        Category customCategory = new Category("MyIncome", CategoryType.INCOME, true, testUser);
        when(categoryRepository.findByNameAndUserId("MyIncome", 1L)).thenReturn(Optional.of(customCategory));
        
        Transaction savedCustom = new Transaction(new BigDecimal("5000.00"), LocalDate.now(), "Salary", customCategory, testUser);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedCustom);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals("MyIncome", response.category());
    }
    
    @Test
    void createTransaction_NullDate_ThrowsBadRequestException() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("5000.00"), null, "Salary", "Salary");
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(request));
    }
    
    @Test
    void createTransaction_FutureDate_ThrowsBadRequestException() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("5000.00"), LocalDate.now().plusDays(1), "Salary", "Salary");
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(request));
    }
    
    @Test
    void createTransaction_InvalidCategory_ThrowsBadRequestException() {
        mockAuthentication();
        TransactionRequest request = new TransactionRequest(new BigDecimal("5000.00"), LocalDate.now(), "Invalid", "Salary");
        
        when(categoryRepository.findByNameAndCustomCategoryFalse("Invalid")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUserId("Invalid", 1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(request));
    }

    @Test
    void getTransactions_NoFilters() {
        mockAuthentication();
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(testTransaction));

        List<TransactionResponse> responses = transactionService.getTransactions(null, null, null);

        assertEquals(1, responses.size());
    }
    
    @Test
    void getTransactions_DateRangeAndCategory() {
        mockAuthentication();
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();
        when(transactionRepository.findByUserIdAndCategoryAndDateBetween(1L, "Salary", startDate, endDate))
                .thenReturn(List.of(testTransaction));

        List<TransactionResponse> responses = transactionService.getTransactions(startDate, endDate, "Salary");

        assertEquals(1, responses.size());
    }
    
    @Test
    void getTransactions_DateRangeOnly() {
        mockAuthentication();
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();
        when(transactionRepository.findByUserIdAndDateBetween(1L, startDate, endDate))
                .thenReturn(List.of(testTransaction));

        List<TransactionResponse> responses = transactionService.getTransactions(startDate, endDate, null);

        assertEquals(1, responses.size());
    }
    
    @Test
    void getTransactions_CategoryOnly() {
        mockAuthentication();
        when(transactionRepository.findByUserIdAndCategory(1L, "Salary"))
                .thenReturn(List.of(testTransaction));

        List<TransactionResponse> responses = transactionService.getTransactions(null, null, "Salary");

        assertEquals(1, responses.size());
    }
    
    @Test
    void getTransactions_OnlyOneDate_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> transactionService.getTransactions(LocalDate.now(), null, null));
        assertThrows(BadRequestException.class, () -> transactionService.getTransactions(null, LocalDate.now(), null));
    }
    
    @Test
    void getTransactions_StartDateAfterEndDate_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> transactionService.getTransactions(LocalDate.now().plusDays(1), LocalDate.now(), null));
    }

    @Test
    void getTransaction_Success() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));

        TransactionResponse response = transactionService.getTransaction(1L);

        assertNotNull(response);
    }
    
    @Test
    void getTransaction_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransaction(1L));
    }

    @Test
    void updateTransaction_Success() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));
        when(categoryRepository.findByNameAndCustomCategoryFalse("Food")).thenReturn(Optional.of(new Category("Food", CategoryType.EXPENSE, false, null)));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionUpdateRequest request = new TransactionUpdateRequest(new BigDecimal("6000.00"), null, "Food", "Updated");
        TransactionResponse response = transactionService.updateTransaction(1L, request);

        assertNotNull(response);
        verify(transactionRepository).save(any(Transaction.class));
    }
    
    @Test
    void updateTransaction_NegativeAmount_ThrowsBadRequestException() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));

        TransactionUpdateRequest request = new TransactionUpdateRequest(new BigDecimal("-100.00"), null, null, null);
        assertThrows(BadRequestException.class, () -> transactionService.updateTransaction(1L, request));
    }

    @Test
    void updateTransaction_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        TransactionUpdateRequest request = new TransactionUpdateRequest(new BigDecimal("6000.00"), null, null, null);
        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(1L, request));
    }

    @Test
    void deleteTransaction_Success() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).delete(testTransaction);
    }
    
    @Test
    void deleteTransaction_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(1L));
    }
    
    @Test
    void getCurrentUser_NotAuthenticated_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> transactionService.getTransaction(1L));
    }
    
    @Test
    void getCurrentUser_UserNotFound_ThrowsResourceNotFoundException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("notfound@test.com");
        when(userRepository.findByUsername("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransaction(1L));
    }

    @Test
    void getCurrentUser_NotAuthenticatedFlag_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> transactionService.getTransactions(null, null, null)
        );
    }

    @Test
    void getTransactions_BlankCategory_ThrowsBadRequestException() {
        mockAuthentication();

        assertThrows(
                BadRequestException.class,
                () -> transactionService.getTransactions(
                        null,
                        null,
                        "   "
                )
        );
    }

    @Test
    void updateTransaction_NoOptionalFields_SavesExistingTransaction() {
        mockAuthentication();

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testTransaction));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        TransactionUpdateRequest request =
                new TransactionUpdateRequest(
                        null,
                        null,
                        null,
                        null
                );

        TransactionResponse response =
                transactionService.updateTransaction(1L, request);

        assertNotNull(response);

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransaction_BlankCategory_DoesNotChangeCategory() {
        mockAuthentication();

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testTransaction));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        TransactionUpdateRequest request =
                new TransactionUpdateRequest(
                        null,
                        null,
                        "   ",
                        null
                );

        TransactionResponse response =
                transactionService.updateTransaction(1L, request);

        assertNotNull(response);

        verify(categoryRepository, never())
                .findByNameAndCustomCategoryFalse(anyString());
    }

    @Test
    void updateTransaction_CustomCategory_Success() {
        mockAuthentication();

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testTransaction));

        Category customCategory =
                new Category(
                        "MyIncome",
                        CategoryType.INCOME,
                        true,
                        testUser
                );

        customCategory.setId(2L);

        when(categoryRepository
                .findByNameAndCustomCategoryFalse("MyIncome"))
                .thenReturn(Optional.empty());

        when(categoryRepository
                .findByNameAndUserId("MyIncome", 1L))
                .thenReturn(Optional.of(customCategory));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        TransactionUpdateRequest request =
                new TransactionUpdateRequest(
                        null,
                        null,
                        "MyIncome",
                        null
                );

        TransactionResponse response =
                transactionService.updateTransaction(1L, request);

        assertNotNull(response);

        verify(categoryRepository)
                .findByNameAndUserId("MyIncome", 1L);

        verify(transactionRepository)
                .save(testTransaction);
    }
}
