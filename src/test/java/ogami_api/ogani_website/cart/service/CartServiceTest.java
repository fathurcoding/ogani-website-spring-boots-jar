package ogami_api.ogani_website.cart.service;

import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.repository.CartRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.exception.InsufficientStockException;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.repository.ProductRepository;
import ogami_api.ogani_website.user.model.User;
import ogami_api.ogani_website.user.model.UserRole;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService.
 * Tests cart operations including add, update, remove, and retrieve.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.CUSTOMER)
                .build();

        testProduct = Product.builder()
                .productId(1)
                .productName("Test Product")
                .price(BigDecimal.valueOf(10000))
                .stock(100)
                .build();

        testCart = Cart.builder()
                .cartId(1)
                .user(testUser)
                .product(testProduct)
                .quantity(2)
                .build();
    }

    @Test
    @DisplayName("Add to cart with valid product should create cart item")
    void addToCart_WithValidProduct_AddsCartItem() {
        // Given: user and product exist, product has enough stock, no existing cart
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUser_UserIdAndProduct_ProductId(1, 1)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When: addToCart is called
        Cart result = cartService.addToCart(1, 1, 2);

        // Then: cart item is created
        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(1);
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getProduct().getProductId()).isEqualTo(1);
        assertThat(result.getUser().getUserId()).isEqualTo(1);

        // Verify interactions
        verify(userRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Add to cart with insufficient stock should throw exception")
    void addToCart_WithInsufficientStock_ThrowsException() {
        // Given: product has insufficient stock
        testProduct.setStock(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // When & Then: addToCart throws InsufficientStockException
        assertThatThrownBy(() -> cartService.addToCart(1, 1, 5))
                .isInstanceOf(InsufficientStockException.class);

        // Verify cart was not saved
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Add to cart with non-existent user should throw DataNotFoundException")
    void addToCart_WithNonExistentUser_ThrowsDataNotFoundException() {
        // Given: user doesn't exist
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then: addToCart throws DataNotFoundException
        assertThatThrownBy(() -> cartService.addToCart(999, 1, 2))
                .isInstanceOf(DataNotFoundException.class);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Update cart quantity with valid quantity should update cart")
    void updateCartQuantity_WithValidQuantity_UpdatesCart() {
        // Given: cart item exists
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        
        Cart updatedCart = Cart.builder()
                .cartId(1)
                .user(testUser)
                .product(testProduct)
                .quantity(5)
                .build();
        
        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        // When: updateCartQuantity is called
        Cart result = cartService.updateCartQuantity(1, 5);

        // Then: quantity is updated
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(5);

        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Remove from cart with valid ID should delete item")
    void removeFromCart_WithValidId_RemovesItem() {
        // Given: cart item exists
        when(cartRepository.existsById(1)).thenReturn(true);
        doNothing().when(cartRepository).deleteById(1);

        // When: removeFromCart is called
        cartService.removeFromCart(1);

        // Then: cart item is deleted
        verify(cartRepository, times(1)).existsById(1);
        verify(cartRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Get cart by user ID should return list of cart items")
    void getCartByUserId_WithValidUser_ReturnsCartItems() {
        // Given: user has cart items
        Cart cart2 = Cart.builder()
                .cartId(2)
                .user(testUser)
                .product(testProduct)
                .quantity(3)
                .build();
        
        List<Cart> cartItems = Arrays.asList(testCart, cart2);
        when(cartRepository.findByUser_UserId(1)).thenReturn(cartItems);

        // When: getCartByUserId is called
        List<Cart> result = cartService.getCartByUserId(1);

        // Then: return list of cart items
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCartId()).isEqualTo(1);
        assertThat(result.get(1).getCartId()).isEqualTo(2);

        verify(cartRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    @DisplayName("Clear cart should remove all user's cart items")
    void clearCart_WithValidUser_RemovesAllItems() {
        // Given: user exists
        doNothing().when(cartRepository).deleteByUser_UserId(1);

        // When: clearCart is called
        cartService.clearCart(1);

        // Then: all cart items are deleted
        verify(cartRepository, times(1)).deleteByUser_UserId(1);
    }
}
