package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.goal.GoalRequest;
import com.sahil.personalfinancemanager.dto.goal.GoalResponse;
import com.sahil.personalfinancemanager.dto.goal.GoalUpdateRequest;
import com.sahil.personalfinancemanager.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(
            GoalService goalService
    ) {
        this.goalService = goalService;
    }


    // =========================================================
    // CREATE GOAL
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse createGoal(
            @Valid @RequestBody GoalRequest request
    ) {

        return goalService.createGoal(request);
    }


    // =========================================================
    // GET ALL GOALS
    // =========================================================

    @GetMapping
    public Map<String, List<GoalResponse>> getGoals() {

        return Map.of(
                "goals",
                goalService.getGoals()
        );
    }


    // =========================================================
    // GET GOAL BY ID
    // =========================================================

    @GetMapping("/{id}")
    public GoalResponse getGoal(
            @PathVariable Long id
    ) {

        return goalService.getGoal(id);
    }


    // =========================================================
    // UPDATE GOAL
    // =========================================================

    @PutMapping("/{id}")
    public GoalResponse updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalUpdateRequest request
    ) {

        return goalService.updateGoal(
                id,
                request
        );
    }


    // =========================================================
    // DELETE GOAL
    // =========================================================

    @DeleteMapping("/{id}")
    public Map<String, String> deleteGoal(
            @PathVariable Long id
    ) {

        goalService.deleteGoal(id);

        return Map.of(
                "message",
                "Goal deleted successfully"
        );
    }
}