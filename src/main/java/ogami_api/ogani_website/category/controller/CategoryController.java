package ogami_api.ogani_website.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.category.dto.BulkCategoryRequest;
import ogami_api.ogani_website.category.dto.CategoryRequest;
import ogami_api.ogani_website.category.dto.CategoryResponse;
import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.category.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * GET /api/categories - Get all categories with optional pagination.
     * Params: page, size, sort
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        
        // If pagination params provided, return paginated response
        if (page != null && size != null) {
            Pageable pageable = createPageable(page, size, sort);
            Page<Category> categoryPage = categoryService.getAllCategories(pageable);
            Page<CategoryResponse> responsePage = categoryPage.map(this::toResponse);
            return ResponseEntity.ok(responsePage);
        }
        
        // Otherwise return all categories
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

    /**
     * POST /api/categories/bulk - Bulk create categories.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<CategoryResponse>> createCategoriesBulk(@Valid @RequestBody BulkCategoryRequest request) {
        List<Category> categories = request.getCategories().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        
        List<Category> created = categoryService.createCategoriesBulk(categories);
        List<CategoryResponse> response = created.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/categories/bulk?ids=1,2,3 - Bulk delete categories.
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteCategoriesBulk(@RequestParam List<Integer> ids) {
        categoryService.deleteCategoriesBulk(ids);
        return ResponseEntity.noContent().build();
    }

    // Helper methods

    private Pageable createPageable(Integer page, Integer size, String sort) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 10;
        
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])) 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, field));
        }
        
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "categoryId"));
    }

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
