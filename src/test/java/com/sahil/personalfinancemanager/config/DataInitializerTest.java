package com.sahil.personalfinancemanager.config;

import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DataInitializer initializer;

    @Test
    void run_WhenDefaultsDoNotExist_CreatesAllDefaults() {
        when(categoryRepository.findByNameAndCustomCategoryFalse(anyString()))
                .thenReturn(Optional.empty());

        initializer.run();

        verify(categoryRepository, times(7))
                .save(any(Category.class));
    }

    @Test
    void run_WhenDefaultsAlreadyExist_DoesNotCreateDuplicates() {
        when(categoryRepository.findByNameAndCustomCategoryFalse(anyString()))
                .thenReturn(Optional.of(mock(Category.class)));

        initializer.run();

        verify(categoryRepository, never()).save(any(Category.class));
    }
}