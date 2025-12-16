package ogami_api.ogani_website.product.service;

import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.category.repository.CategoryRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.exception.InsufficientStockException;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer untuk Product.
 * Business logic untuk CRUD operations dan stock management.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Get all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID.
     */
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product", id));
    }

    /**
     * Search products by name.
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    /**
     * Get products by category.
     */
    public List<Product> getProductsByCategory(Integer categoryId) {
        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new DataNotFoundException("Category", categoryId);
        }
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    /**
     * Get products dengan stok tersedia.
     */
    public List<Product> getAvailableProducts() {
        return productRepository.findByStockGreaterThan(0);
    }

    /**
     * Create new product.
     */
    public Product createProduct(Product product) {
        // Validasi: product name wajib
        if (product.getProductName() == null || product.getProductName().isBlank()) {
            throw new IllegalArgumentException("Product name wajib diisi");
        }

        // Validasi: price harus > 0
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Harga produk harus lebih dari 0");
        }

        // Validasi: category exists
        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            var category = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category", product.getCategory().getCategoryId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    /**
     * Update existing product.
     */
    public Product updateProduct(Integer id, Product updatedProduct) {
        Product existing = getProductById(id);

        // Update fields
        if (updatedProduct.getProductName() != null && !updatedProduct.getProductName().isBlank()) {
            existing.setProductName(updatedProduct.getProductName());
        }

        if (updatedProduct.getDescription() != null) {
            existing.setDescription(updatedProduct.getDescription());
        }

        if (updatedProduct.getPrice() != null && updatedProduct.getPrice().doubleValue() > 0) {
            existing.setPrice(updatedProduct.getPrice());
        }

        if (updatedProduct.getStock() != null && updatedProduct.getStock() >= 0) {
            existing.setStock(updatedProduct.getStock());
        }

        if (updatedProduct.getProductImage() != null) {
            existing.setProductImage(updatedProduct.getProductImage());
        }

        // Update category if provided
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getCategoryId() != null) {
            var category = categoryRepository.findById(updatedProduct.getCategory().getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category", updatedProduct.getCategory().getCategoryId()));
            existing.setCategory(category);
        }

        return productRepository.save(existing);
    }

    /**
     * Delete product.
     */
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new DataNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Reduce stock ketika ada pembelian (digunakan oleh OrderService).
     */
    public void reduceStock(Integer productId, Integer quantity) {
        Product product = getProductById(productId);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    product.getProductName(),
                    quantity,
                    product.getStock()
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
