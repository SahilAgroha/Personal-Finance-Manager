package com.sahil.personalfinancemanager.dto.category;

import com.sahil.personalfinancemanager.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        String name,

        @NotNull(message = "Category type is required")
        CategoryType type
) {
}