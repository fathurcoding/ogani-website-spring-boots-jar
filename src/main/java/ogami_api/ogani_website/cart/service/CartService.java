package ogami_api.ogani_website.cart.service;

import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.repository.CartRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.exception.InsufficientStockException;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.repository.ProductRepository;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer untuk Cart.
 * Business logic untuk shopping cart operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Get all cart items untuk user tertentu.
     */
    public List<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUser_UserId(userId);
    }

    /**
     * Add product to cart.
     */
    public Cart addToCart(Integer userId, Integer productId, Integer quantity) {
        // Validasi user exists
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        // Validasi product exists
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product", productId));

        // Validasi stock
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    product.getProductName(),
                    quantity,
                    product.getStock()
            );
        }

        // Check if product sudah ada di cart
        var existingCart = cartRepository.findByUser_UserIdAndProduct_ProductId(userId, productId);

        if (existingCart.isPresent()) {
            // Update quantity
            Cart cart = existingCart.get();
            int newQuantity = cart.getQuantity() + quantity;

            // Validate stock lagi
            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException(
                        product.getProductName(),
                        newQuantity,
                        product.getStock()
                );
            }

            cart.setQuantity(newQuantity);
            return cartRepository.save(cart);
        } else {
            // Create new cart item
            Cart cart = Cart.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartRepository.save(cart);
        }
    }

    /**
     * Update quantity cart item.
     */
    public Cart updateCartQuantity(Integer cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new DataNotFoundException("Cart item", cartId));

        // Validasi quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0");
        }

        // Validasi stock
        Product product = cart.getProduct();
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    product.getProductName(),
                    quantity,
                    product.getStock()
            );
        }

        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    /**
     * Remove item from cart.
     */
    public void removeFromCart(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new DataNotFoundException("Cart item", cartId);
        }
        cartRepository.deleteById(cartId);
    }

    /**
     * Clear all cart items untuk user tertentu.
     */
    public void clearCart(Integer userId) {
        cartRepository.deleteByUser_UserId(userId);
    }

    /**
     * Calculate total price dari cart.
     */
    public BigDecimal calculateCartTotal(Integer userId) {
        List<Cart> cartItems = getCartByUserId(userId);

        return cartItems.stream()
                .map(cart -> cart.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get cart item count untuk user.
     */
    public Long getCartItemCount(Integer userId) {
        return cartRepository.countByUser_UserId(userId);
    }
}
