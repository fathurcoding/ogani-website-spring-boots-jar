package ogami_api.ogani_website.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.cart.dto.CartItemResponse;
import ogami_api.ogani_website.cart.dto.CartRequest;
import ogami_api.ogani_website.cart.dto.CartResponse;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Cart API.
 * Note: userId akan diambil dari JWT token (authenticated user).
 * Untuk sementara menggunakan hardcoded userId untuk testing.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart - Get current user's cart.
     * TODO: Extract userId dari JWT token
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        // Hardcoded untuk testing - nanti ambil dari JWT
        Integer userId = 1;  // TODO: Get from authentication

        List<Cart> cartItems = cartService.getCartByUserId(userId);
        CartResponse response = toCartResponse(cartItems);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/cart - Add product to cart.
     */
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody CartRequest request) {
        Integer userId = 1;  // TODO: Get from authentication

        Cart cart = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toCartItemResponse(cart));
    }

    /**
     * PUT /api/cart/{id} - Update cart item quantity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartQuantity(id, quantity);
        return ResponseEntity.ok(toCartItemResponse(cart));
    }

    /**
     * DELETE /api/cart/{id} - Remove item from cart.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer id) {
        cartService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/cart - Clear all cart items.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        Integer userId = 1;  // TODO: Get from authentication
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Helper methods

    private CartItemResponse toCartItemResponse(Cart cart) {
        BigDecimal subtotal = cart.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .productId(cart.getProduct().getProductId())
                .productName(cart.getProduct().getProductName())
                .productPrice(cart.getProduct().getPrice())
                .productImage(cart.getProduct().getProductImage())
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    private CartResponse toCartResponse(List<Cart> cartItems) {
        List<CartItemResponse> items = cartItems.stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalItems(items.size())
                .totalPrice(totalPrice)
                .build();
    }
}
