package com.sahil.personalfinancemanager.repository;

import com.sahil.personalfinancemanager.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository
        extends JpaRepository<SavingsGoal, Long> {

    // =========================================================
    // FIND ALL GOALS OF CURRENT USER
    // =========================================================

    List<SavingsGoal> findByUserIdOrderByTargetDateAsc(
            Long userId
    );


    // =========================================================
    // FIND ONE GOAL OF CURRENT USER
    // =========================================================

    Optional<SavingsGoal> findByIdAndUserId(
            Long id,
            Long userId
    );
}