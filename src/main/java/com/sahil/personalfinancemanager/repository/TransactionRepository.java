package com.sahil.personalfinancemanager.repository;

import com.sahil.personalfinancemanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // =========================================================
    // GET ALL TRANSACTIONS FOR CURRENT USER
    // =========================================================

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.category c
            WHERE t.user.id = :userId
            ORDER BY t.date DESC, t.id DESC
            """)
    List<Transaction> findAllByUserId(
            @Param("userId") Long userId
    );


    // =========================================================
    // GET TRANSACTION BY ID FOR CURRENT USER
    // =========================================================

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.category c
            WHERE t.id = :transactionId
            AND t.user.id = :userId
            """)
    Optional<Transaction> findByIdAndUserId(
            @Param("transactionId") Long transactionId,
            @Param("userId") Long userId
    );


    // =========================================================
    // FILTER BY CATEGORY
    // =========================================================

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.category c
            WHERE t.user.id = :userId
            AND c.name = :category
            ORDER BY t.date DESC, t.id DESC
            """)
    List<Transaction> findByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") String category
    );


    // =========================================================
    // FILTER BY DATE RANGE
    // =========================================================

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.category c
            WHERE t.user.id = :userId
            AND t.date >= :startDate
            AND t.date <= :endDate
            ORDER BY t.date DESC, t.id DESC
            """)
    List<Transaction> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    // =========================================================
    // FILTER BY CATEGORY + DATE RANGE
    // =========================================================

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.category c
            WHERE t.user.id = :userId
            AND c.name = :category
            AND t.date >= :startDate
            AND t.date <= :endDate
            ORDER BY t.date DESC, t.id DESC
            """)
    List<Transaction> findByUserIdAndCategoryAndDateBetween(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    // =========================================================
    // CHECK WHETHER CATEGORY HAS TRANSACTIONS
    // =========================================================

    boolean existsByCategoryId(Long categoryId);


    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.id = :userId
        AND t.date >= :startDate
        AND t.date <= :endDate
        AND t.category.type = com.sahil.personalfinancemanager.entity.CategoryType.INCOME
        """)
    BigDecimal calculateIncomeForGoal(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.id = :userId
        AND t.date >= :startDate
        AND t.date <= :endDate
        AND t.category.type = com.sahil.personalfinancemanager.entity.CategoryType.EXPENSE
        """)
    BigDecimal calculateExpenseForGoal(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =========================================================
// MONTHLY REPORT
// =========================================================

    @Query("""
        SELECT c.name, COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        JOIN t.category c
        WHERE t.user.id = :userId
        AND t.date >= :startDate
        AND t.date <= :endDate
        AND c.type = :type
        GROUP BY c.name
        ORDER BY c.name
        """)
    List<Object[]> getCategoryTotals(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") com.sahil.personalfinancemanager.entity.CategoryType type
    );
}