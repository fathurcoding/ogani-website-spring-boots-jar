package ogami_api.ogani_website.category.repository;

import ogami_api.ogani_website.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository untuk Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Find category by name.
     */
    Optional<Category> findByCategoryName(String categoryName);
    
    /**
     * Check if category name already exists.
     */
    Boolean existsByCategoryName(String categoryName);
}
