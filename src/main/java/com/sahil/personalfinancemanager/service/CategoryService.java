package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.category.CategoryRequest;
import com.sahil.personalfinancemanager.dto.category.CategoryResponse;
import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.exception.ConflictException;
import com.sahil.personalfinancemanager.exception.ResourceNotFoundException;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import com.sahil.personalfinancemanager.repository.TransactionRepository;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CREATE CUSTOM CATEGORY
    // =========================================================

    @Transactional
    public CategoryResponse createCategory(
            CategoryRequest request
    ) {

        User user = getCurrentUser();

        String name = request.name().trim();

        // -----------------------------------------------------
        // Validate name
        // -----------------------------------------------------

        if (name.isEmpty()) {
            throw new BadRequestException(
                    "Category name cannot be empty"
            );
        }

        // -----------------------------------------------------
        // Default category cannot be recreated as custom
        // -----------------------------------------------------

        if (categoryRepository
                .findByNameAndCustomCategoryFalse(name)
                .isPresent()) {

            throw new ConflictException(
                    "Category already exists"
            );
        }

        // -----------------------------------------------------
        // Check custom category including soft-deleted records
        // -----------------------------------------------------

        Optional<Category> existingCategory =
                categoryRepository.findCustomCategoryIncludingDeleted(
                        name,
                        user.getId()
                );

        if (existingCategory.isPresent()) {

            Category category = existingCategory.get();

            // -------------------------------------------------
            // Active custom category already exists
            // -------------------------------------------------

            if (!category.isDeleted()) {

                throw new ConflictException(
                        "Category already exists"
                );
            }

            // -------------------------------------------------
            // Restore soft-deleted category
            // -------------------------------------------------

            category.setDeleted(false);
            category.setType(request.type());

            Category restoredCategory =
                    categoryRepository.save(category);

            return toResponse(restoredCategory);
        }

        // -----------------------------------------------------
        // Create new custom category
        // -----------------------------------------------------

        Category category = new Category(
                name,
                request.type(),
                true,
                user
        );

        Category savedCategory =
                categoryRepository.save(category);

        return toResponse(savedCategory);
    }


    // =========================================================
    // GET ALL ACCESSIBLE CATEGORIES
    // =========================================================

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {

        User user = getCurrentUser();

        return categoryRepository
                .findAccessibleCategories(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // UPDATE CUSTOM CATEGORY
    // =========================================================

    @Transactional
    public CategoryResponse updateCategory(
            String categoryName,
            CategoryRequest request
    ) {

        User user = getCurrentUser();

        String name = categoryName.trim();

        // -----------------------------------------------------
        // Find category
        // Including deleted categories so we can give the
        // correct "not found" behaviour
        // -----------------------------------------------------

        Category category =
                categoryRepository
                        .findCustomCategoryIncludingDeleted(
                                name,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        // -----------------------------------------------------
        // Deleted category cannot be updated
        // -----------------------------------------------------

        if (category.isDeleted()) {

            throw new ResourceNotFoundException(
                    "Category not found"
            );
        }

        // -----------------------------------------------------
        // Default categories cannot be updated
        // -----------------------------------------------------

        if (!category.isCustomCategory()) {

            throw new BadRequestException(
                    "Default categories cannot be updated"
            );
        }

        String newName = request.name().trim();

        // -----------------------------------------------------
        // Validate new name
        // -----------------------------------------------------

        if (newName.isEmpty()) {

            throw new BadRequestException(
                    "Category name cannot be empty"
            );
        }

        // -----------------------------------------------------
        // Name is being changed
        // -----------------------------------------------------

        if (!newName.equalsIgnoreCase(category.getName())) {

            // -------------------------------------------------
            // Cannot use a default category name
            // -------------------------------------------------

            if (categoryRepository
                    .findByNameAndCustomCategoryFalse(newName)
                    .isPresent()) {

                throw new ConflictException(
                        "Category already exists"
                );
            }

            // -------------------------------------------------
            // Cannot duplicate user's custom category
            // -------------------------------------------------

            if (categoryRepository.existsCustomCategory(
                    newName,
                    user.getId()
            )) {

                throw new ConflictException(
                        "Category already exists"
                );
            }

            category.setName(newName);
        }

        // -----------------------------------------------------
        // Update type
        // -----------------------------------------------------

        category.setType(request.type());

        Category updatedCategory =
                categoryRepository.save(category);

        return toResponse(updatedCategory);
    }


    // =========================================================
    // DELETE CUSTOM CATEGORY
    // =========================================================

    @Transactional
    public void deleteCategory(
            String categoryName
    ) {

        User user = getCurrentUser();

        String name = categoryName.trim();

        // -----------------------------------------------------
        // Check whether it is a default category
        // -----------------------------------------------------

        if (categoryRepository
                .findByNameAndCustomCategoryFalse(name)
                .isPresent()) {

            throw new BadRequestException(
                    "Default categories cannot be deleted"
            );
        }

        // -----------------------------------------------------
        // Find user's custom category
        // -----------------------------------------------------

        Category category =
                categoryRepository
                        .findCustomCategoryIncludingDeleted(
                                name,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        // -----------------------------------------------------
        // Already deleted
        // -----------------------------------------------------

        if (category.isDeleted()) {

            throw new ResourceNotFoundException(
                    "Category not found"
            );
        }

        // -----------------------------------------------------
        // Cannot delete category used by transactions
        // -----------------------------------------------------

        if (transactionRepository
                .existsByCategoryId(category.getId())) {

            throw new ConflictException(
                    "Category is currently used by transactions"
            );
        }

        // -----------------------------------------------------
        // Soft delete
        // -----------------------------------------------------

        category.setDeleted(true);

        categoryRepository.save(category);
    }


    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // -----------------------------------------------------
        // Authentication validation
        // -----------------------------------------------------

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getName()
        )) {

            throw new BadRequestException(
                    "User is not authenticated"
            );
        }

        // -----------------------------------------------------
        // Get username from JWT/SecurityContext
        // -----------------------------------------------------

        String username =
                authentication.getName();

        // -----------------------------------------------------
        // Find user
        // -----------------------------------------------------

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private CategoryResponse toResponse(
            Category category
    ) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.isCustomCategory()
        );
    }
}