package ogami_api.ogani_website.product.service;

import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.category.repository.CategoryRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Tests CRUD operations and pagination.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private Product newProduct;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .categoryId(1)
                .categoryName("Test Category")
                .build();

        testProduct = Product.builder()
                .productId(1)
                .productName("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(10000))
                .stock(100)
                .productImage("http://example.com/image.jpg")
                .category(testCategory)
                .build();

        newProduct = Product.builder()
                .productName("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(15000))
                .stock(50)
                .productImage("http://example.com/new.jpg")
                .category(testCategory)
                .build();
    }

    @Test
    @DisplayName("Get product by ID with valid ID should return product")
    void getProductById_WithValidId_ReturnsProduct() {
        // Given: product exists
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // When: getProductById is called
        Product result = productService.getProductById(1);

        // Then: return product
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1);
        assertThat(result.getProductName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));

        verify(productRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Get product by ID with invalid ID should throw DataNotFoundException")
    void getProductById_WithInvalidId_ThrowsDataNotFoundException() {
        // Given: product doesn't exist
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then: getProductById throws DataNotFoundException
        assertThatThrownBy(() -> productService.getProductById(999))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Create product with valid data should create product")
    void createProduct_WithValidData_CreatesProduct() {
        // Given: valid product
        Product savedProduct = Product.builder()
                .productId(2)
                .productName("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(15000))
                .stock(50)
                .category(testCategory)
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When: createProduct is called
        Product result = productService.createProduct(newProduct);

        // Then: product is created
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(2);
        assertThat(result.getProductName()).isEqualTo("New Product");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15000));

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product with valid data should update product")
    void updateProduct_WithValidData_UpdatesProduct() {
        // Given: product exists
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        
        Product updatedProduct = Product.builder()
                .productId(1)
                .productName("Updated Product")
                .price(BigDecimal.valueOf(20000))
                .stock(75)
                .category(testCategory)
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When: updateProduct is called
        Product productUpdate = Product.builder()
                .productName("Updated Product")
                .price(BigDecimal.valueOf(20000))
                .stock(75)
                .category(testCategory)
                .build();
        
        Product result = productService.updateProduct(1, productUpdate);

        // Then: product is updated
        assertThat(result).isNotNull();
        assertThat(result.getProductName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product with valid ID should delete product")
    void deleteProduct_WithValidId_DeletesProduct() {
        // Given: product exists
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));

        // When: deleteProduct is called
        productService.deleteProduct(1);

        // Then: product is deleted
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    @DisplayName("Get all products with pagination should return paged products")
    void getAllProducts_WithPagination_ReturnsPagedProducts() {
        // Given: products exist
        Product product2 = Product.builder()
                .productId(2)
                .productName("Product 2")
                .price(BigDecimal.valueOf(15000))
                .build();
        
        List<Product> products = Arrays.asList(testProduct, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());
        
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // When: getAllProducts is called with pagination
        Page<Product> result = productService.getAllProducts(PageRequest.of(0, 10));

        // Then: return paged products
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getProductId()).isEqualTo(1);

        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }
}
