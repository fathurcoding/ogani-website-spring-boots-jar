package ogami_api.ogani_website.category.service;

import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.category.repository.CategoryRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService.
 * Tests CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private Category newCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .categoryId(1)
                .categoryName("Test Category")
                .build();

        newCategory = Category.builder()
                .categoryName("New Category")
                .build();
    }

    @Test
    @DisplayName("Get category by ID with valid ID should return category")
    void getCategoryById_WithValidId_ReturnsCategory() {
        // Given: category exists
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // When: getCategoryById is called
        Category result = categoryService.getCategoryById(1);

        // Then: return category
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(1);
        assertThat(result.getCategoryName()).isEqualTo("Test Category");

        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Create category with valid data should create category")
    void createCategory_WithValidData_CreatesCategory() {
        // Given: valid category
        Category savedCategory = Category.builder()
                .categoryId(2)
                .categoryName("New Category")
                .build();
        
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When: createCategory is called
        Category result = categoryService.createCategory(newCategory);

        // Then: category is created
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(2);
        assertThat(result.getCategoryName()).isEqualTo("New Category");

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Update category with valid data should update category")
    void updateCategory_WithValidData_UpdatesCategory() {
        // Given: category exists
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        
        Category updatedCategory = Category.builder()
                .categoryId(1)
                .categoryName("Updated Category")
                .build();
        
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // When: updateCategory is called
        Category categoryUpdate = Category.builder()
                .categoryName("Updated Category")
                .build();
        
        Category result = categoryService.updateCategory(1, categoryUpdate);

        // Then: category is updated
        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("Updated Category");

        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Delete category with valid ID should delete category")
    void deleteCategory_WithValidId_DeletesCategory() {
        // Given: category exists
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(any(Category.class));

        // When: deleteCategory is called
        categoryService.deleteCategory(1);

        // Then: category is deleted
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).delete(testCategory);
    }
}
