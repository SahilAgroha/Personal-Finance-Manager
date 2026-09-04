package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.goal.GoalRequest;
import com.sahil.personalfinancemanager.dto.goal.GoalResponse;
import com.sahil.personalfinancemanager.dto.goal.GoalUpdateRequest;
import com.sahil.personalfinancemanager.entity.SavingsGoal;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.exception.ResourceNotFoundException;
import com.sahil.personalfinancemanager.repository.SavingsGoalRepository;
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
public class GoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "password", "Test User", "1234567890");
        testUser.setId(1L);
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
    void createGoal_Success() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(30);
        GoalRequest request = new GoalRequest("New Car", new BigDecimal("5000.00"), targetDate, LocalDate.now());
        
        SavingsGoal savedGoal = new SavingsGoal("New Car", new BigDecimal("5000.00"), targetDate, LocalDate.now(), testUser);
        savedGoal.setId(1L);
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(savedGoal);

        GoalResponse response = goalService.createGoal(request);

        assertNotNull(response);
        assertEquals("New Car", response.goalName());
        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }
    
    @Test
    void createGoal_EmptyName_ThrowsBadRequestException() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(30);
        GoalRequest request = new GoalRequest("   ", new BigDecimal("5000.00"), targetDate, LocalDate.now());

        assertThrows(BadRequestException.class, () -> goalService.createGoal(request));
    }

    @Test
    void createGoal_TargetDateInPast_ThrowsBadRequestException() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().minusDays(1);
        GoalRequest request = new GoalRequest("New Car", new BigDecimal("5000.00"), targetDate, LocalDate.now());

        assertThrows(BadRequestException.class, () -> goalService.createGoal(request));
    }

    @Test
    void createGoal_StartDateAfterTargetDate_ThrowsBadRequestException() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(5);
        LocalDate startDate = LocalDate.now().plusDays(10);
        GoalRequest request = new GoalRequest("New Car", new BigDecimal("5000.00"), targetDate, startDate);

        assertThrows(BadRequestException.class, () -> goalService.createGoal(request));
    }

    @Test
    void getGoals_Success() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(30);
        SavingsGoal goal = new SavingsGoal("New Car", new BigDecimal("5000.00"), targetDate, LocalDate.now(), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByUserIdOrderByTargetDateAsc(1L)).thenReturn(List.of(goal));

        List<GoalResponse> responses = goalService.getGoals();

        assertEquals(1, responses.size());
        assertEquals("New Car", responses.get(0).goalName());
    }

    @Test
    void getGoal_Success() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(30);
        SavingsGoal goal = new SavingsGoal("New Car", new BigDecimal("5000.00"), targetDate, LocalDate.now(), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));
        when(transactionRepository.calculateIncomeForGoal(anyLong(), any(), any())).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.calculateExpenseForGoal(anyLong(), any(), any())).thenReturn(new BigDecimal("200.00"));

        GoalResponse response = goalService.getGoal(1L);

        assertNotNull(response);
        assertEquals("New Car", response.goalName());
        assertEquals(0, new BigDecimal("800.00").compareTo(response.currentProgress()));
    }

    @Test
    void getGoal_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.getGoal(1L));
    }

    @Test
    void updateGoal_Success() {
        mockAuthentication();
        LocalDate targetDate = LocalDate.now().plusDays(30);
        SavingsGoal existingGoal = new SavingsGoal("Old Goal", new BigDecimal("5000.00"), targetDate, LocalDate.now(), testUser);
        existingGoal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingGoal));
        
        GoalUpdateRequest request = new GoalUpdateRequest("New Goal", new BigDecimal("6000.00"), targetDate.plusDays(10));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(existingGoal);

        GoalResponse response = goalService.updateGoal(1L, request);

        assertEquals("New Goal", response.goalName());
        assertEquals(new BigDecimal("6000.00"), response.targetAmount());
        assertEquals(targetDate.plusDays(10), response.targetDate());
        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }

    @Test
    void updateGoal_EmptyName_ThrowsBadRequestException() {
        mockAuthentication();
        SavingsGoal existingGoal = new SavingsGoal("Old Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        existingGoal.setId(1L);
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingGoal));
        
        GoalUpdateRequest request = new GoalUpdateRequest("   ", null, null);

        assertThrows(BadRequestException.class, () -> goalService.updateGoal(1L, request));
    }

    @Test
    void updateGoal_TargetDateInPast_ThrowsBadRequestException() {
        mockAuthentication();
        SavingsGoal existingGoal = new SavingsGoal("Old Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        existingGoal.setId(1L);
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingGoal));
        
        GoalUpdateRequest request = new GoalUpdateRequest("New Goal", null, LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> goalService.updateGoal(1L, request));
    }

    @Test
    void updateGoal_StartDateAfterTargetDate_ThrowsBadRequestException() {
        mockAuthentication();
        LocalDate startDate = LocalDate.now().plusDays(10);
        SavingsGoal existingGoal = new SavingsGoal("Old Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), startDate, testUser);
        existingGoal.setId(1L);
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingGoal));
        
        GoalUpdateRequest request = new GoalUpdateRequest("New Goal", null, LocalDate.now().plusDays(5));

        assertThrows(BadRequestException.class, () -> goalService.updateGoal(1L, request));
    }

    @Test
    void updateGoal_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        GoalUpdateRequest request = new GoalUpdateRequest("New Goal", null, null);

        assertThrows(ResourceNotFoundException.class, () -> goalService.updateGoal(1L, request));
    }

    @Test
    void deleteGoal_Success() {
        mockAuthentication();
        SavingsGoal existingGoal = new SavingsGoal("Old Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        existingGoal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingGoal));

        goalService.deleteGoal(1L);

        verify(savingsGoalRepository).delete(existingGoal);
    }
    
    @Test
    void deleteGoal_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.deleteGoal(1L));
    }

    @Test
    void toResponse_CalculatesCorrectlyWithNullIncomeExpense() {
        mockAuthentication();
        SavingsGoal goal = new SavingsGoal("Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));
        when(transactionRepository.calculateIncomeForGoal(anyLong(), any(), any())).thenReturn(null);
        when(transactionRepository.calculateExpenseForGoal(anyLong(), any(), any())).thenReturn(null);

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.currentProgress()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.progressPercentage()));
        assertEquals(0, new BigDecimal("5000.00").compareTo(response.remainingAmount()));
    }
    
    @Test
    void toResponse_TargetAmountZero_ReturnsZeroPercentage() {
        mockAuthentication();
        SavingsGoal goal = new SavingsGoal("Goal", BigDecimal.ZERO, LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));
        when(transactionRepository.calculateIncomeForGoal(anyLong(), any(), any())).thenReturn(new BigDecimal("100.00"));
        when(transactionRepository.calculateExpenseForGoal(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.progressPercentage()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.remainingAmount()));
    }

    @Test
    void toResponse_ProgressExceedsTarget_CapsPercentageAt100() {
        mockAuthentication();
        SavingsGoal goal = new SavingsGoal("Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now(), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));
        when(transactionRepository.calculateIncomeForGoal(anyLong(), any(), any())).thenReturn(new BigDecimal("6000.00"));
        when(transactionRepository.calculateExpenseForGoal(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(0, new BigDecimal("100.00").compareTo(response.progressPercentage()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.remainingAmount()));
    }
    
    @Test
    void toResponse_StartDateAfterToday_ReturnsZeroProgress() {
        mockAuthentication();
        SavingsGoal goal = new SavingsGoal("Goal", new BigDecimal("5000.00"), LocalDate.now().plusDays(30), LocalDate.now().plusDays(5), testUser);
        goal.setId(1L);
        
        when(savingsGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.currentProgress()));
        verify(transactionRepository, never()).calculateIncomeForGoal(anyLong(), any(), any());
    }

    @Test
    void getCurrentUser_NotAuthenticatedFlag_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> goalService.getGoals()
        );
    }

    @Test
    void createGoal_NullStartDate_UsesToday() {
        mockAuthentication();

        LocalDate targetDate = LocalDate.now().plusDays(30);

        GoalRequest request =
                new GoalRequest(
                        "New Goal",
                        new BigDecimal("5000.00"),
                        targetDate,
                        null
                );

        SavingsGoal savedGoal =
                new SavingsGoal(
                        "New Goal",
                        new BigDecimal("5000.00"),
                        targetDate,
                        LocalDate.now(),
                        testUser
                );

        savedGoal.setId(1L);

        when(savingsGoalRepository.save(any(SavingsGoal.class)))
                .thenReturn(savedGoal);

        GoalResponse response = goalService.createGoal(request);

        assertNotNull(response);
        assertEquals("New Goal", response.goalName());

        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }

    @Test
    void updateGoal_NoOptionalFields_SavesExistingGoal() {
        mockAuthentication();

        LocalDate startDate = LocalDate.now();
        LocalDate targetDate = LocalDate.now().plusDays(30);

        SavingsGoal existingGoal =
                new SavingsGoal(
                        "Old Goal",
                        new BigDecimal("5000.00"),
                        targetDate,
                        startDate,
                        testUser
                );

        existingGoal.setId(1L);

        when(savingsGoalRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingGoal));

        when(savingsGoalRepository.save(any(SavingsGoal.class)))
                .thenReturn(existingGoal);

        GoalUpdateRequest request =
                new GoalUpdateRequest(
                        null,
                        null,
                        null
                );

        GoalResponse response =
                goalService.updateGoal(1L, request);

        assertNotNull(response);
        assertEquals("Old Goal", response.goalName());

        verify(savingsGoalRepository).save(existingGoal);
    }

    @Test
    void toResponse_TargetDateInPast_UsesTargetDateAsEndDate() {
        mockAuthentication();

        LocalDate targetDate = LocalDate.now().minusDays(5);
        LocalDate startDate = LocalDate.now().minusDays(10);

        SavingsGoal goal =
                new SavingsGoal(
                        "Past Goal",
                        new BigDecimal("5000.00"),
                        targetDate,
                        startDate,
                        testUser
                );

        goal.setId(1L);

        when(savingsGoalRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(goal));

        when(transactionRepository.calculateIncomeForGoal(
                anyLong(), eq(startDate), eq(targetDate)))
                .thenReturn(new BigDecimal("1000.00"));

        when(transactionRepository.calculateExpenseForGoal(
                anyLong(), eq(startDate), eq(targetDate)))
                .thenReturn(new BigDecimal("200.00"));

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(
                0,
                new BigDecimal("800.00")
                        .compareTo(response.currentProgress())
        );
    }

    @Test
    void toResponse_NegativeProgress_ReturnsZeroPercentage() {
        mockAuthentication();

        SavingsGoal goal =
                new SavingsGoal(
                        "Goal",
                        new BigDecimal("5000.00"),
                        LocalDate.now().plusDays(30),
                        LocalDate.now(),
                        testUser
                );

        goal.setId(1L);

        when(savingsGoalRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(goal));

        when(transactionRepository.calculateIncomeForGoal(
                anyLong(), any(), any()))
                .thenReturn(new BigDecimal("100.00"));

        when(transactionRepository.calculateExpenseForGoal(
                anyLong(), any(), any()))
                .thenReturn(new BigDecimal("500.00"));

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(response.progressPercentage())
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(response.currentProgress().max(BigDecimal.ZERO))
        );
    }

    @Test
    void toResponse_NullTargetAmount_ReturnsZeroPercentage() {
        mockAuthentication();

        SavingsGoal goal =
                new SavingsGoal(
                        "Goal",
                        null,
                        LocalDate.now().plusDays(30),
                        LocalDate.now(),
                        testUser
                );

        goal.setId(1L);

        when(savingsGoalRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(goal));

        when(transactionRepository.calculateIncomeForGoal(
                anyLong(), any(), any()))
                .thenReturn(new BigDecimal("100.00"));

        when(transactionRepository.calculateExpenseForGoal(
                anyLong(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.getGoal(1L);

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(response.progressPercentage())
        );
    }

    @Test
    void getCurrentUser_UserNotFound_ThrowsResourceNotFoundException() {
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn("notfound@test.com");

        when(userRepository.findByUsername("notfound@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> goalService.getGoals()
        );
    }


    @Test
    void getCurrentUser_NullAuthentication_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThrows(
                BadRequestException.class,
                () -> goalService.getGoals()
        );
    }

    @Test
    void getCurrentUser_Unauthenticated_ThrowsException() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThrows(
                BadRequestException.class,
                () -> goalService.getGoals()
        );
    }

}
