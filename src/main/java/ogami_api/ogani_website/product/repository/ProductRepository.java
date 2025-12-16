package ogami_api.ogani_website.product.repository;

import ogami_api.ogani_website.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Find products by category ID.
     */
    List<Product> findByCategory_CategoryId(Integer categoryId);

    /**
     * Search products by name (case-insensitive).
     */
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    /**
     * Find products dengan stok tersedia.
     */
    List<Product> findByStockGreaterThan(Integer stock);
}
