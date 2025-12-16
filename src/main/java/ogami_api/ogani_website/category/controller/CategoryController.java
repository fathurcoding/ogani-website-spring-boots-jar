package ogami_api.ogani_website.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.category.dto.CategoryRequest;
import ogami_api.ogani_website.category.dto.CategoryResponse;
import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Category API.
 * Endpoints untuk manage categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * GET /api/categories - Get all categories.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/categories/{id} - Get category by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(toResponse(category));
    }

    /**
     * POST /api/categories - Create new category (Admin only).
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = toEntity(request);
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    /**
     * PUT /api/categories/{id} - Update category (Admin only).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {
        Category category = toEntity(request);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(toResponse(updated));
    }

    /**
     * DELETE /api/categories/{id} - Delete category (Admin only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Helper methods untuk mapping Entity <-> DTO

    private Category toEntity(CategoryRequest request) {
        return Category.builder()
                .categoryName(request.getCategoryName())
                .image(request.getImage())
                .build();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .image(category.getImage())
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .build();
    }
}
