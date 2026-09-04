package com.sahil.personalfinancemanager.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    // =========================================================
    // USER
    // =========================================================

    @Test
    void user_DefaultConstructorAndSetters() {
        User user = new User();

        user.setId(1L);
        user.setUsername("test@test.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    void user_ParameterizedConstructor() {
        User user = new User(
                "test@test.com",
                "encodedPassword",
                "Test User",
                "1234567890"
        );

        assertEquals("test@test.com", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals("1234567890", user.getPhoneNumber());
    }


    // =========================================================
    // CATEGORY
    // =========================================================

    @Test
    void category_DefaultConstructorAndSetters() {
        Category category = new Category();

        User user = new User();
        user.setId(1L);

        category.setId(1L);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);
        category.setCustomCategory(true);
        category.setDeleted(true);
        category.setUser(user);

        assertEquals(1L, category.getId());
        assertEquals("Food", category.getName());
        assertEquals(CategoryType.EXPENSE, category.getType());
        assertTrue(category.isCustomCategory());
        assertTrue(category.isDeleted());
        assertSame(user, category.getUser());
    }

    @Test
    void category_ParameterizedConstructor() {
        User user = new User();
        user.setId(1L);

        Category category = new Category(
                "Salary",
                CategoryType.INCOME,
                true,
                user
        );

        assertEquals("Salary", category.getName());
        assertEquals(CategoryType.INCOME, category.getType());
        assertTrue(category.isCustomCategory());
        assertFalse(category.isDeleted());
        assertSame(user, category.getUser());
    }

    @Test
    void category_DefaultValues() {
        Category category = new Category();

        assertFalse(category.isCustomCategory());
        assertFalse(category.isDeleted());
    }


    // =========================================================
    // CATEGORY TYPE
    // =========================================================

    @Test
    void categoryType_AllValues() {
        assertEquals(2, CategoryType.values().length);
        assertEquals(CategoryType.INCOME, CategoryType.valueOf("INCOME"));
        assertEquals(CategoryType.EXPENSE, CategoryType.valueOf("EXPENSE"));
    }


    // =========================================================
    // TRANSACTION
    // =========================================================

    @Test
    void transaction_DefaultConstructorAndSetters() {
        Transaction transaction = new Transaction();

        User user = new User();
        Category category = new Category(
                "Food",
                CategoryType.EXPENSE,
                false,
                user
        );

        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("500.00");

        transaction.setId(1L);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setDescription("Lunch");

        assertEquals(1L, transaction.getId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(date, transaction.getDate());
        assertSame(category, transaction.getCategory());
        assertSame(user, transaction.getUser());
        assertEquals("Lunch", transaction.getDescription());
    }

    @Test
    void transaction_ParameterizedConstructor() {
        User user = new User(
                "test@test.com",
                "password",
                "Test User",
                "1234567890"
        );

        Category category = new Category(
                "Salary",
                CategoryType.INCOME,
                false,
                user
        );

        LocalDate date = LocalDate.of(2024, 1, 10);
        BigDecimal amount = new BigDecimal("5000.00");

        Transaction transaction = new Transaction(
                amount,
                date,
                "Monthly salary",
                category,
                user
        );

        assertEquals(amount, transaction.getAmount());
        assertEquals(date, transaction.getDate());
        assertEquals("Monthly salary", transaction.getDescription());
        assertSame(category, transaction.getCategory());
        assertSame(user, transaction.getUser());
    }


    // =========================================================
    // SAVINGS GOAL
    // =========================================================

    @Test
    void savingsGoal_DefaultConstructorAndSetters() {
        SavingsGoal goal = new SavingsGoal();

        User user = new User();
        user.setId(1L);

        BigDecimal amount = new BigDecimal("10000.00");
        LocalDate targetDate = LocalDate.of(2027, 1, 1);
        LocalDate startDate = LocalDate.of(2026, 1, 1);

        goal.setId(1L);
        goal.setGoalName("New Car");
        goal.setTargetAmount(amount);
        goal.setTargetDate(targetDate);
        goal.setStartDate(startDate);
        goal.setUser(user);

        assertEquals(1L, goal.getId());
        assertEquals("New Car", goal.getGoalName());
        assertEquals(amount, goal.getTargetAmount());
        assertEquals(targetDate, goal.getTargetDate());
        assertEquals(startDate, goal.getStartDate());
        assertSame(user, goal.getUser());
    }

    @Test
    void savingsGoal_ParameterizedConstructor() {
        User user = new User(
                "test@test.com",
                "password",
                "Test User",
                "1234567890"
        );

        BigDecimal amount = new BigDecimal("5000.00");
        LocalDate targetDate = LocalDate.of(2027, 6, 1);
        LocalDate startDate = LocalDate.of(2026, 9, 1);

        SavingsGoal goal = new SavingsGoal(
                "Vacation",
                amount,
                targetDate,
                startDate,
                user
        );

        assertEquals("Vacation", goal.getGoalName());
        assertEquals(amount, goal.getTargetAmount());
        assertEquals(targetDate, goal.getTargetDate());
        assertEquals(startDate, goal.getStartDate());
        assertSame(user, goal.getUser());
    }
}