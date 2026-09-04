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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class GoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public GoalService(
            SavingsGoalRepository savingsGoalRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CREATE GOAL
    // =========================================================

    @Transactional
    public GoalResponse createGoal(
            GoalRequest request
    ) {

        User user = getCurrentUser();

        String goalName = request.goalName().trim();

        if (goalName.isEmpty()) {
            throw new BadRequestException(
                    "Goal name cannot be empty"
            );
        }

        LocalDate startDate =
                request.startDate() != null
                        ? request.startDate()
                        : LocalDate.now();

        LocalDate targetDate =
                request.targetDate();

        // -----------------------------------------------------
        // Target date cannot be in the past
        // -----------------------------------------------------

        if (targetDate.isBefore(LocalDate.now())) {

            throw new BadRequestException(
                    "Target date cannot be in the past"
            );
        }

        // -----------------------------------------------------
        // Start date cannot be after target date
        // -----------------------------------------------------

        if (startDate.isAfter(targetDate)) {

            throw new BadRequestException(
                    "Start date cannot be after target date"
            );
        }

        // -----------------------------------------------------
        // Create goal
        // -----------------------------------------------------

        SavingsGoal goal = new SavingsGoal(
                goalName,
                request.targetAmount(),
                targetDate,
                startDate,
                user
        );

        SavingsGoal savedGoal =
                savingsGoalRepository.save(goal);

        return toResponse(savedGoal);
    }


    // =========================================================
    // GET ALL GOALS
    // =========================================================

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals() {

        User user = getCurrentUser();

        return savingsGoalRepository
                .findByUserIdOrderByTargetDateAsc(
                        user.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET GOAL BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public GoalResponse getGoal(
            Long goalId
    ) {

        User user = getCurrentUser();

        SavingsGoal goal =
                savingsGoalRepository
                        .findByIdAndUserId(
                                goalId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Goal not found"
                                )
                        );

        return toResponse(goal);
    }


    // =========================================================
    // UPDATE GOAL
    // =========================================================

    @Transactional
    public GoalResponse updateGoal(
            Long goalId,
            GoalUpdateRequest request
    ) {

        User user = getCurrentUser();

        SavingsGoal goal =
                savingsGoalRepository
                        .findByIdAndUserId(
                                goalId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Goal not found"
                                )
                        );

        // -----------------------------------------------------
        // Update goal name if provided
        // -----------------------------------------------------

        if (request.goalName() != null) {

            String goalName = request.goalName().trim();

            if (goalName.isEmpty()) {

                throw new BadRequestException(
                        "Goal name cannot be empty"
                );
            }

            goal.setGoalName(goalName);
        }

        // -----------------------------------------------------
        // Update target amount if provided
        // -----------------------------------------------------

        if (request.targetAmount() != null) {

            goal.setTargetAmount(
                    request.targetAmount()
            );
        }

        // -----------------------------------------------------
        // Update target date if provided
        // -----------------------------------------------------

        if (request.targetDate() != null) {

            if (request.targetDate()
                    .isBefore(LocalDate.now())) {

                throw new BadRequestException(
                        "Target date cannot be in the past"
                );
            }

            if (goal.getStartDate()
                    .isAfter(request.targetDate())) {

                throw new BadRequestException(
                        "Start date cannot be after target date"
                );
            }

            goal.setTargetDate(
                    request.targetDate()
            );
        }

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        SavingsGoal updatedGoal =
                savingsGoalRepository.save(goal);

        return toResponse(updatedGoal);
    }


    // =========================================================
    // DELETE GOAL
    // =========================================================

    @Transactional
    public void deleteGoal(
            Long goalId
    ) {

        User user = getCurrentUser();

        SavingsGoal goal =
                savingsGoalRepository
                        .findByIdAndUserId(
                                goalId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Goal not found"
                                )
                        );

        savingsGoalRepository.delete(goal);
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private GoalResponse toResponse(
            SavingsGoal goal
    ) {

        BigDecimal currentProgress =
                calculateCurrentProgress(goal);

        BigDecimal progressPercentage =
                calculateProgressPercentage(
                        currentProgress,
                        goal.getTargetAmount()
                );

        BigDecimal remainingAmount =
                calculateRemainingAmount(
                        currentProgress,
                        goal.getTargetAmount()
                );

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgress,
                progressPercentage,
                remainingAmount
        );
    }


    // =========================================================
    // CALCULATE CURRENT PROGRESS
    // =========================================================

    private BigDecimal calculateCurrentProgress(
            SavingsGoal goal
    ) {

        LocalDate today = LocalDate.now();

        // Do not calculate transactions beyond today.
        LocalDate endDate =
                goal.getTargetDate().isBefore(today)
                        ? goal.getTargetDate()
                        : today;

        // -----------------------------------------------------
        // If target/start dates produce no valid period
        // -----------------------------------------------------

        if (goal.getStartDate().isAfter(endDate)) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        // -----------------------------------------------------
        // Total income
        // -----------------------------------------------------

        BigDecimal income =
                transactionRepository.calculateIncomeForGoal(
                        goal.getUser().getId(),
                        goal.getStartDate(),
                        endDate
                );

        // -----------------------------------------------------
        // Total expenses
        // -----------------------------------------------------

        BigDecimal expenses =
                transactionRepository.calculateExpenseForGoal(
                        goal.getUser().getId(),
                        goal.getStartDate(),
                        endDate
                );

        if (income == null) {
            income = BigDecimal.ZERO;
        }

        if (expenses == null) {
            expenses = BigDecimal.ZERO;
        }

        // -----------------------------------------------------
        // Net savings
        // -----------------------------------------------------

        return income
                .subtract(expenses)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // =========================================================
    // CALCULATE PROGRESS %
    // =========================================================

    private BigDecimal calculateProgressPercentage(
            BigDecimal currentProgress,
            BigDecimal targetAmount
    ) {

        if (targetAmount == null
                || targetAmount.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        // -----------------------------------------------------
        // Do not show negative progress
        // -----------------------------------------------------

        BigDecimal progress =
                currentProgress.max(BigDecimal.ZERO);

        return progress
                .divide(
                        targetAmount,
                        4,
                        RoundingMode.HALF_UP
                )
                .multiply(new BigDecimal("100"))
                .min(new BigDecimal("100"))
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // =========================================================
    // CALCULATE REMAINING AMOUNT
    // =========================================================

    private BigDecimal calculateRemainingAmount(
            BigDecimal currentProgress,
            BigDecimal targetAmount
    ) {

        BigDecimal progress =
                currentProgress.max(BigDecimal.ZERO);

        return targetAmount
                .subtract(progress)
                .max(BigDecimal.ZERO)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getName()
        )) {

            throw new BadRequestException(
                    "User is not authenticated"
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }
}