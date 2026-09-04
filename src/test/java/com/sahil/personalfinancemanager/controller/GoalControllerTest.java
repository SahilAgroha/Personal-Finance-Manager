package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.goal.GoalRequest;
import com.sahil.personalfinancemanager.dto.goal.GoalResponse;
import com.sahil.personalfinancemanager.dto.goal.GoalUpdateRequest;
import com.sahil.personalfinancemanager.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
    }

    private GoalResponse sampleGoalResponse() {
        return new GoalResponse(1L, "Car", new BigDecimal("5000.00"),
                LocalDate.now().plusDays(30), LocalDate.now(),
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("5000.00"));
    }

    @Test
    void getGoals_Success() throws Exception {
        when(goalService.getGoals()).thenReturn(List.of(sampleGoalResponse()));

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals[0].goalName").value("Car"));
    }

    @Test
    void getGoal_Success() throws Exception {
        when(goalService.getGoal(1L)).thenReturn(sampleGoalResponse());

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Car"));
    }

    @Test
    void createGoal_Success() throws Exception {
        when(goalService.createGoal(any(GoalRequest.class))).thenReturn(sampleGoalResponse());

        String body = String.format("{\"goalName\":\"Car\",\"targetAmount\":5000.00,\"targetDate\":\"%s\",\"startDate\":\"%s\"}",
                LocalDate.now().plusDays(30), LocalDate.now());

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalName").value("Car"));
    }

    @Test
    void updateGoal_Success() throws Exception {
        GoalResponse updated = new GoalResponse(1L, "Updated Car", new BigDecimal("6000.00"),
                LocalDate.now().plusDays(60), LocalDate.now(),
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("6000.00"));
        when(goalService.updateGoal(eq(1L), any(GoalUpdateRequest.class))).thenReturn(updated);

        String body = String.format("{\"goalName\":\"Updated Car\",\"targetAmount\":6000.00,\"targetDate\":\"%s\"}",
                LocalDate.now().plusDays(60));

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Updated Car"));
    }

    @Test
    void deleteGoal_Success() throws Exception {
        doNothing().when(goalService).deleteGoal(1L);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }
}
