package com.sahil.personalfinancemanager.config;

import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryDataInitializer
        implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        createIfNotExists(
                "Salary",
                CategoryType.INCOME
        );

        createIfNotExists(
                "Food",
                CategoryType.EXPENSE
        );

        createIfNotExists(
                "Rent",
                CategoryType.EXPENSE
        );

        createIfNotExists(
                "Transportation",
                CategoryType.EXPENSE
        );

        createIfNotExists(
                "Entertainment",
                CategoryType.EXPENSE
        );

        createIfNotExists(
                "Healthcare",
                CategoryType.EXPENSE
        );

        createIfNotExists(
                "Utilities",
                CategoryType.EXPENSE
        );
    }

    private void createIfNotExists(
            String name,
            CategoryType type
    ) {

        if (categoryRepository
                .findByNameAndCustomCategoryFalse(name)
                .isEmpty()) {

            Category category = new Category(
                    name,
                    type,
                    false,
                    null
            );

            categoryRepository.save(category);
        }
    }
}