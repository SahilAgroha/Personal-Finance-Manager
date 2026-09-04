package com.sahil.personalfinancemanager.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sahil.personalfinancemanager.entity.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,

        @JsonProperty("custom")
        boolean isCustom
) {
}