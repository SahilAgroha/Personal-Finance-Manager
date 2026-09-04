package com.sahil.personalfinancemanager.config;

import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        createDefault("Salary", CategoryType.INCOME);

        createDefault("Food", CategoryType.EXPENSE);
        createDefault("Rent", CategoryType.EXPENSE);
        createDefault("Transportation", CategoryType.EXPENSE);
        createDefault("Entertainment", CategoryType.EXPENSE);
        createDefault("Healthcare", CategoryType.EXPENSE);
        createDefault("Utilities", CategoryType.EXPENSE);
    }

    private void createDefault(
            String name,
            CategoryType type
    ) {

        if (categoryRepository
                .findByNameAndCustomCategoryFalse(name)
                .isEmpty()) {

            categoryRepository.save(
                    new Category(
                            name,
                            type,
                            false,
                            null
                    )
            );
        }
    }
}