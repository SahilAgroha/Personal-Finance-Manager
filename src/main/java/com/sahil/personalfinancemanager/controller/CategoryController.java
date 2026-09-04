package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.category.CategoryRequest;
import com.sahil.personalfinancemanager.dto.category.CategoryResponse;
import com.sahil.personalfinancemanager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @GetMapping
    public Map<String, List<CategoryResponse>> getCategories() {

        return Map.of(
                "categories",
                categoryService.getCategories()
        );
    }


    // =========================================================
    // CREATE CUSTOM CATEGORY
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        return categoryService.createCategory(request);
    }


    // =========================================================
    // UPDATE CUSTOM CATEGORY
    // =========================================================

    @PutMapping("/{name}")
    public CategoryResponse updateCategory(
            @PathVariable String name,
            @Valid @RequestBody CategoryRequest request
    ) {

        return categoryService.updateCategory(
                name,
                request
        );
    }


    // =========================================================
    // DELETE CUSTOM CATEGORY
    // =========================================================

    @DeleteMapping("/{name}")
    public Map<String, String> deleteCategory(
            @PathVariable String name
    ) {

        categoryService.deleteCategory(name);

        return Map.of(
                "message",
                "Category deleted successfully"
        );
    }
}