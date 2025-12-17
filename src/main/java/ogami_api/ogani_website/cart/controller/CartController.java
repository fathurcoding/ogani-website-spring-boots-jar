package ogami_api.ogani_website.cart.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ogami_api.ogani_website.cart.dto.CartItemResponse;
import ogami_api.ogani_website.cart.dto.CartRequest;
import ogami_api.ogani_website.cart.dto.CartResponse;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Cart API.
 * UserId diambil dari JWT token (authenticated user).
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shopping Cart", description = "Shopping cart operations (authentication required)")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart - Get current user's cart.
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);

        List<Cart> cartItems = cartService.getCartByUserId(userId);
        CartResponse response = toCartResponse(cartItems);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/cart - Add product to cart.
     */
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @Valid @RequestBody CartRequest request,
            Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);

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
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Helper methods

    /**
     * Extract userId from JWT authentication principal.
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        return (Integer) authentication.getPrincipal();
    }

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
