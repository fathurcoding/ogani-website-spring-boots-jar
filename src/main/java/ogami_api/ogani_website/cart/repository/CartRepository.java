package ogami_api.ogani_website.cart.repository;

import ogami_api.ogani_website.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    /**
     * Find all cart items for a specific user.
     */
    List<Cart> findByUser_UserId(Integer userId);

    /**
     * Find specific cart item (user + product combination).
     */
    Optional<Cart> findByUser_UserIdAndProduct_ProductId(Integer userId, Integer productId);

    /**
     * Delete all cart items for a user (after checkout).
     */
    void deleteByUser_UserId(Integer userId);
    
    /**
     * Count cart items for a user.
     */
    Long countByUser_UserId(Integer userId);
}
