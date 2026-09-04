package com.sahil.personalfinancemanager.repository;

import com.sahil.personalfinancemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // =========================================================
    // FIND DEFAULT CATEGORY BY NAME
    // =========================================================
    //
    // customCategory = false means this is a system/default
    // category such as Salary, Food, Rent, etc.
    //
    Optional<Category> findByNameAndCustomCategoryFalse(
            String name
    );


    // =========================================================
    // FIND USER'S CUSTOM CATEGORY
    // INCLUDING SOFT-DELETED CATEGORIES
    // =========================================================
    //
    // We intentionally do NOT check deleted = false here.
    // This allows the service to restore a previously deleted
    // category instead of creating a duplicate.
    //
    @Query("""
            SELECT c
            FROM Category c
            WHERE c.name = :name
            AND c.user.id = :userId
            AND c.customCategory = true
            """)
    Optional<Category> findCustomCategoryIncludingDeleted(
            @Param("name") String name,
            @Param("userId") Long userId
    );


    // =========================================================
    // CHECK IF ACTIVE CUSTOM CATEGORY EXISTS
    // =========================================================
    //
    @Query("""
            SELECT COUNT(c) > 0
            FROM Category c
            WHERE c.name = :name
            AND c.user.id = :userId
            AND c.customCategory = true
            AND c.deleted = false
            """)
    boolean existsCustomCategory(
            @Param("name") String name,
            @Param("userId") Long userId
    );


    // =========================================================
    // GET ALL ACCESSIBLE CATEGORIES
    // =========================================================
    //
    // User can see:
    //
    // 1. All default categories
    // 2. Their own custom categories
    //
    // Deleted custom categories are hidden.
    //
    @Query("""
            SELECT c
            FROM Category c
            WHERE c.deleted = false
            AND (
                c.customCategory = false
                OR c.user.id = :userId
            )
            ORDER BY c.customCategory ASC, c.name ASC
            """)
    List<Category> findAccessibleCategories(
            @Param("userId") Long userId
    );


    // =========================================================
    // FIND CATEGORY BY NAME AND USER
    // =========================================================
    //
    // This can be useful for other services as well.
    //
    @Query("""
            SELECT c
            FROM Category c
            WHERE c.name = :name
            AND c.user.id = :userId
            AND c.deleted = false
            """)
    Optional<Category> findByNameAndUserId(
            @Param("name") String name,
            @Param("userId") Long userId
    );
}