package ogami_api.ogani_website.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.product.dto.BulkProductRequest;
import ogami_api.ogani_website.product.dto.ProductRequest;
import ogami_api.ogani_website.product.dto.ProductResponse;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Product API.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Get all products with optional pagination.
     * Params: page (default 0), size (default 10), sort (default productId,asc)
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        
        // If pagination params provided, return paginated response
        if (page != null && size != null) {
            Pageable pageable = createPageable(page, size, sort);
            Page<Product> productPage = productService.getAllProducts(pageable);
            Page<ProductResponse> responsePage = productPage.map(this::toResponse);
            return ResponseEntity.ok(responsePage);
        }
        
        // Otherwise return all products
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/{id} - Get product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(toResponse(product));
    }

    /**
     * GET /api/products/search?q=keyword - Search products.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String q) {
        List<Product> products = productService.searchProducts(q);
        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/category/{categoryId} - Get products by category.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/available - Get products with stock.
     */
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/products - Create new product (Admin only).
     */
    @PostMapping

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = toEntity(request);
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    /**
     * PUT /api/products/{id} - Update product (Admin only).
     */
    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        Product product = toEntity(request);
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(toResponse(updated));
    }

    /**
     * DELETE /api/products/{id} - Delete product (Admin only).
     */
    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/products/bulk - Bulk create products.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<ProductResponse>> createProductsBulk(@Valid @RequestBody BulkProductRequest request) {
        List<Product> products = request.getProducts().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        
        List<Product> created = productService.createProductsBulk(products);
        List<ProductResponse> response = created.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/products/bulk?ids=1,2,3 - Bulk delete products.
     */
    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteProductsBulk(@RequestParam List<Integer> ids) {
        productService.deleteProductsBulk(ids);
        return ResponseEntity.noContent().build();
    }

    // Helper methods

    private Pageable createPageable(Integer page, Integer size, String sort) {
        // Default values
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 10;
        
        // Parse sort parameter (format: "field,direction" or just "field")
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])) 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, field));
        }
        
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "productId"));
    }

    private Product toEntity(ProductRequest request) {
        return Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .productImage(request.getProductImage())
                .category(Category.builder().categoryId(request.getCategoryId()).build())
                .build();
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .productImage(product.getProductImage())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .build();
    }
}
