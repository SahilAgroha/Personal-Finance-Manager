package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.category.CategoryRequest;
import com.sahil.personalfinancemanager.dto.category.CategoryResponse;
import com.sahil.personalfinancemanager.entity.Category;
import com.sahil.personalfinancemanager.entity.CategoryType;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.BadRequestException;
import com.sahil.personalfinancemanager.exception.ConflictException;
import com.sahil.personalfinancemanager.exception.ResourceNotFoundException;
import com.sahil.personalfinancemanager.repository.CategoryRepository;
import com.sahil.personalfinancemanager.repository.TransactionRepository;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "password", "Test User", "1234567890");
        testUser.setId(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findByUsername("test@test.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void createCategory_Success() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("NewCategory", CategoryType.INCOME);
        when(categoryRepository.findByNameAndCustomCategoryFalse("NewCategory")).thenReturn(Optional.empty());
        when(categoryRepository.findCustomCategoryIncludingDeleted("NewCategory", 1L)).thenReturn(Optional.empty());

        Category savedCategory = new Category("NewCategory", CategoryType.INCOME, true, testUser);
        savedCategory.setId(1L);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("NewCategory", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_EmptyName_ThrowsBadRequestException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("   ", CategoryType.INCOME);
        
        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void createCategory_DefaultCategoryExists_ThrowsConflictException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("Salary", CategoryType.INCOME);
        when(categoryRepository.findByNameAndCustomCategoryFalse("Salary")).thenReturn(Optional.of(new Category()));

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void createCategory_CustomCategoryExistsAndActive_ThrowsConflictException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("ExistingCategory", CategoryType.INCOME);
        when(categoryRepository.findByNameAndCustomCategoryFalse("ExistingCategory")).thenReturn(Optional.empty());
        
        Category activeCategory = new Category("ExistingCategory", CategoryType.INCOME, true, testUser);
        activeCategory.setDeleted(false);
        when(categoryRepository.findCustomCategoryIncludingDeleted("ExistingCategory", 1L)).thenReturn(Optional.of(activeCategory));

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void createCategory_CustomCategoryExistsAndDeleted_RestoresCategory() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("DeletedCategory", CategoryType.EXPENSE);
        when(categoryRepository.findByNameAndCustomCategoryFalse("DeletedCategory")).thenReturn(Optional.empty());
        
        Category deletedCategory = new Category("DeletedCategory", CategoryType.INCOME, true, testUser);
        deletedCategory.setDeleted(true);
        when(categoryRepository.findCustomCategoryIncludingDeleted("DeletedCategory", 1L)).thenReturn(Optional.of(deletedCategory));
        
        Category restoredCategory = new Category("DeletedCategory", CategoryType.EXPENSE, true, testUser);
        restoredCategory.setDeleted(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(restoredCategory);

        CategoryResponse response = categoryService.createCategory(request);

        assertEquals("DeletedCategory", response.name());
        assertEquals(CategoryType.EXPENSE, response.type());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getCategories_Success() {
        mockAuthentication();
        Category category = new Category("Food", CategoryType.EXPENSE, false, null);
        category.setId(1L);
        when(categoryRepository.findAccessibleCategories(1L)).thenReturn(List.of(category));

        List<CategoryResponse> responses = categoryService.getCategories();

        assertEquals(1, responses.size());
        assertEquals("Food", responses.get(0).name());
    }

    @Test
    void updateCategory_Success() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("UpdatedCategory", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setId(1L);
        existingCategory.setDeleted(false);
        
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByNameAndCustomCategoryFalse("UpdatedCategory")).thenReturn(Optional.empty());
        when(categoryRepository.existsCustomCategory("UpdatedCategory", 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        CategoryResponse response = categoryService.updateCategory("OldCategory", request);

        assertEquals("UpdatedCategory", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_EmptyName_ThrowsBadRequestException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("  ", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setDeleted(false);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        
        assertThrows(BadRequestException.class, () -> categoryService.updateCategory("OldCategory", request));
    }

    @Test
    void updateCategory_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("UpdatedCategory", CategoryType.EXPENSE);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory("OldCategory", request));
    }

    @Test
    void updateCategory_Deleted_ThrowsResourceNotFoundException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("UpdatedCategory", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setDeleted(true);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory("OldCategory", request));
    }

    @Test
    void updateCategory_DefaultCategory_ThrowsBadRequestException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("UpdatedCategory", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, false, null);
        existingCategory.setDeleted(false);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        
        assertThrows(BadRequestException.class, () -> categoryService.updateCategory("OldCategory", request));
    }
    
    @Test
    void updateCategory_NewNameIsDefault_ThrowsConflictException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("Salary", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setDeleted(false);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByNameAndCustomCategoryFalse("Salary")).thenReturn(Optional.of(new Category()));
        
        assertThrows(ConflictException.class, () -> categoryService.updateCategory("OldCategory", request));
    }

    @Test
    void updateCategory_NewNameIsExistingCustom_ThrowsConflictException() {
        mockAuthentication();
        CategoryRequest request = new CategoryRequest("OtherCategory", CategoryType.EXPENSE);
        Category existingCategory = new Category("OldCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setDeleted(false);
        when(categoryRepository.findCustomCategoryIncludingDeleted("OldCategory", 1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByNameAndCustomCategoryFalse("OtherCategory")).thenReturn(Optional.empty());
        when(categoryRepository.existsCustomCategory("OtherCategory", 1L)).thenReturn(true);
        
        assertThrows(ConflictException.class, () -> categoryService.updateCategory("OldCategory", request));
    }

    @Test
    void deleteCategory_Success() {
        mockAuthentication();
        Category existingCategory = new Category("MyCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setId(1L);
        existingCategory.setDeleted(false);
        
        when(categoryRepository.findByNameAndCustomCategoryFalse("MyCategory")).thenReturn(Optional.empty());
        when(categoryRepository.findCustomCategoryIncludingDeleted("MyCategory", 1L)).thenReturn(Optional.of(existingCategory));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory("MyCategory");

        assertTrue(existingCategory.isDeleted());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void deleteCategory_IsDefaultCategory_ThrowsBadRequestException() {
        mockAuthentication();
        when(categoryRepository.findByNameAndCustomCategoryFalse("Salary")).thenReturn(Optional.of(new Category()));

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory("Salary"));
    }

    @Test
    void deleteCategory_NotFound_ThrowsResourceNotFoundException() {
        mockAuthentication();
        when(categoryRepository.findByNameAndCustomCategoryFalse("MyCategory")).thenReturn(Optional.empty());
        when(categoryRepository.findCustomCategoryIncludingDeleted("MyCategory", 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory("MyCategory"));
    }
    
    @Test
    void deleteCategory_AlreadyDeleted_ThrowsResourceNotFoundException() {
        mockAuthentication();
        Category existingCategory = new Category("MyCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setDeleted(true);
        when(categoryRepository.findByNameAndCustomCategoryFalse("MyCategory")).thenReturn(Optional.empty());
        when(categoryRepository.findCustomCategoryIncludingDeleted("MyCategory", 1L)).thenReturn(Optional.of(existingCategory));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory("MyCategory"));
    }

    @Test
    void deleteCategory_UsedInTransactions_ThrowsConflictException() {
        mockAuthentication();
        Category existingCategory = new Category("MyCategory", CategoryType.INCOME, true, testUser);
        existingCategory.setId(1L);
        existingCategory.setDeleted(false);
        
        when(categoryRepository.findByNameAndCustomCategoryFalse("MyCategory")).thenReturn(Optional.empty());
        when(categoryRepository.findCustomCategoryIncludingDeleted("MyCategory", 1L)).thenReturn(Optional.of(existingCategory));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory("MyCategory"));
    }

    @Test
    void getCurrentUser_NotAuthenticated_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> categoryService.getCategories());
    }
    
    @Test
    void getCurrentUser_Anonymous_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        assertThrows(BadRequestException.class, () -> categoryService.getCategories());
    }

    @Test
    void getCurrentUser_UserNotFound_ThrowsResourceNotFoundException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("notfound@test.com");
        when(userRepository.findByUsername("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategories());
    }

    @Test
    void getCurrentUser_NotAuthenticatedFlag_ThrowsBadRequestException() {
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> categoryService.getCategories()
        );
    }

    @Test
    void updateCategory_SameName_UpdatesTypeWithoutCheckingDuplicates() {
        mockAuthentication();

        Category existingCategory =
                new Category(
                        "Food",
                        CategoryType.EXPENSE,
                        true,
                        testUser
                );

        existingCategory.setId(1L);
        existingCategory.setDeleted(false);

        when(categoryRepository
                .findCustomCategoryIncludingDeleted("Food", 1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(existingCategory);

        CategoryRequest request =
                new CategoryRequest(
                        "Food",
                        CategoryType.INCOME
                );

        CategoryResponse response =
                categoryService.updateCategory("Food", request);

        assertEquals("Food", response.name());
        assertEquals(CategoryType.INCOME, response.type());

        verify(categoryRepository, never())
                .existsCustomCategory(anyString(), anyLong());

        verify(categoryRepository)
                .save(existingCategory);
    }
}
