package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.category.CategoryRequest;
import com.sahil.personalfinancemanager.dto.category.CategoryResponse;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void getCategories_Success() throws Exception {
        CategoryResponse response = new CategoryResponse(1L, "Food", CategoryType.EXPENSE, false);
        when(categoryService.getCategories()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].name").value("Food"))
                .andExpect(jsonPath("$.categories[0].type").value("EXPENSE"));
    }

    @Test
    void createCategory_Success() throws Exception {
        CategoryResponse response = new CategoryResponse(2L, "SideHustle", CategoryType.INCOME, true);
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"SideHustle\",\"type\":\"INCOME\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SideHustle"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void updateCategory_Success() throws Exception {
        CategoryResponse response = new CategoryResponse(2L, "UpdatedHustle", CategoryType.INCOME, true);
        when(categoryService.updateCategory(eq("SideHustle"), any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/categories/SideHustle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedHustle\",\"type\":\"INCOME\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedHustle"));
    }

    @Test
    void deleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategory("SideHustle");

        mockMvc.perform(delete("/api/categories/SideHustle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }
}
