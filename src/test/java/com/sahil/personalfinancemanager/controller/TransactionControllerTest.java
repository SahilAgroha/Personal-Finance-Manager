package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.transaction.TransactionRequest;
import com.sahil.personalfinancemanager.dto.transaction.TransactionResponse;
import com.sahil.personalfinancemanager.dto.transaction.TransactionUpdateRequest;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.service.TransactionService;
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
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    private TransactionResponse sampleResponse() {
        return new TransactionResponse(1L, new BigDecimal("5000.00"), LocalDate.now(), "Salary", "Income", CategoryType.INCOME);
    }

    @Test
    void getTransactions_Success() throws Exception {
        when(transactionService.getTransactions(null, null, null)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].category").value("Salary"));
    }

    @Test
    void getTransactionsWithParams_Success() throws Exception {
        LocalDate date = LocalDate.now();
        when(transactionService.getTransactions(date, date, "Salary")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/transactions")
                        .param("startDate", date.toString())
                        .param("endDate", date.toString())
                        .param("category", "Salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].category").value("Salary"));
    }

    @Test
    void getTransaction_Success() throws Exception {
        when(transactionService.getTransaction(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(sampleResponse());

        String body = String.format("{\"amount\":5000.00,\"date\":\"%s\",\"category\":\"Salary\",\"description\":\"Income\"}",
                LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test
    void updateTransaction_Success() throws Exception {
        TransactionResponse updated = new TransactionResponse(1L, new BigDecimal("6000.00"), LocalDate.now(), "Salary", "Updated", CategoryType.INCOME);
        when(transactionService.updateTransaction(eq(1L), any(TransactionUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":6000.00,\"category\":\"Salary\",\"description\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void deleteTransaction_Success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }
}
