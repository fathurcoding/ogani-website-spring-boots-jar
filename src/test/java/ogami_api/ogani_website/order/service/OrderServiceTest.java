package ogami_api.ogani_website.order.service;

import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.repository.CartRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.order.model.Order;
import ogami_api.ogani_website.order.model.OrderDetail;
import ogami_api.ogani_website.order.model.OrderStatus;
import ogami_api.ogani_website.order.repository.OrderRepository;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.service.ProductService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 * Tests order creation, status updates, and cancellation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private Order testOrder;

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

        testOrder = Order.builder()
                .orderId(1)
                .user(testUser)
                .invoiceCode("INV-TEST-001")
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(20000))
                .orderTime(LocalDateTime.now())
                .receiverName("Test Receiver")
                .receiverPhone("081234567890")
                .shippingAddress("Test Address")
                .build();
    }

    @Test
    @DisplayName("Create order from cart with valid cart should create order successfully")
    void createOrderFromCart_WithValidCart_CreatesOrder() {
        // Given: user has cart items
        List<Cart> cartItems = Arrays.asList(testCart);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser_UserId(1)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(productService).reduceStock(anyInt(), anyInt());
        doNothing().when(cartRepository).deleteByUser_UserId(1);

        // When: createOrderFromCart is called
        Order result = orderService.createOrderFromCart(
                1, "Test Receiver", "081234567890", "Test Address"
        );

        // Then: order is created
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));

        // Verify interactions
        verify(userRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).findByUser_UserId(1);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productService, times(1)).reduceStock(anyInt(), anyInt());
        verify(cartRepository, times(1)).deleteByUser_UserId(1);
    }

    @Test
    @DisplayName("Create order from empty cart should throw exception")
    void createOrderFromCart_WithEmptyCart_ThrowsException() {
        // Given: cart is empty
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser_UserId(1)).thenReturn(Arrays.asList());

        // When & Then: createOrderFromCart throws exception
        assertThatThrownBy(() -> orderService.createOrderFromCart(
                1, "Test", "081234567890", "Address"
        ))
                .isInstanceOf(IllegalArgumentException.class);

        // Verify no order was created
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Update order status with valid order should update status")
    void updateOrderStatus_WithValidOrder_UpdatesStatus() {
        // Given: order exists
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        
        Order updatedOrder = Order.builder()
                .orderId(1)
                .user(testUser)
                .orderStatus(OrderStatus.PROCESSING)
                .totalPrice(BigDecimal.valueOf(20000))
                .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // When: updateOrderStatus is called
        Order result = orderService.updateOrderStatus(1, OrderStatus.PROCESSING);

        // Then: status is updated
        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);

        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Cancel order with PENDING status should cancel order")
    void cancelOrder_WithPendingOrder_CancelsAndRestoresStock() {
        // Given: order is PENDING
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        
        Order cancelledOrder = Order.builder()
                .orderId(1)
                .user(testUser)
                .orderStatus(OrderStatus.CANCELLED)
                .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        // When: cancelOrder is called
        Order result = orderService.cancelOrder(1);

        // Then: order is cancelled
        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Get orders by user ID should return list of orders")
    void getOrdersByUserId_WithValidUser_ReturnsOrders() {
        // Given: user has orders
        Order order2 = Order.builder()
                .orderId(2)
                .user(testUser)
                .orderStatus(OrderStatus.COMPLETED)
                .build();
        
        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderRepository.findByUser_UserId(1)).thenReturn(orders);

        // When: getOrdersByUserId is called
        List<Order> result = orderService.getOrdersByUserId(1);

        // Then: return list of orders
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrderId()).isEqualTo(1);
        assertThat(result.get(1).getOrderId()).isEqualTo(2);

        verify(orderRepository, times(1)).findByUser_UserId(1);
    }
}
