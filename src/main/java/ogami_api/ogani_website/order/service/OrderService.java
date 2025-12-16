package ogami_api.ogani_website.order.service;

import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.cart.repository.CartRepository;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.exception.InsufficientStockException;
import ogami_api.ogani_website.order.model.Order;
import ogami_api.ogani_website.order.model.OrderDetail;
import ogami_api.ogani_website.order.model.OrderStatus;
import ogami_api.ogani_website.order.repository.OrderRepository;
import ogami_api.ogani_website.product.model.Product;
import ogami_api.ogani_website.product.service.ProductService;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer untuk Order.
 * Business logic untuk checkout dan order management.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    /**
     * Get all orders untuk user tertentu.
     */
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    /**
     * Get order by ID.
     */
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order", orderId));
    }

    /**
     * Get order by invoice code.
     */
    public Order getOrderByInvoiceCode(String invoiceCode) {
        return orderRepository.findByInvoiceCode(invoiceCode)
                .orElseThrow(() -> new DataNotFoundException("Order dengan invoice " + invoiceCode + " tidak ditemukan"));
    }

    /**
     * Get orders by status.
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    /**
     * Create order dari cart (checkout process).
     */
    public Order createOrderFromCart(Integer userId, String receiverName, String receiverPhone, String shippingAddress) {
        // Validasi user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        // Get cart items
        List<Cart> cartItems = cartRepository.findByUser_UserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart kosong, tidak bisa checkout");
        }

        // Validasi stock untuk semua items
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        product.getProductName(),
                        cartItem.getQuantity(),
                        product.getStock()
                );
            }
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .invoiceCode(generateInvoiceCode())
                .orderStatus(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .shippingAddress(shippingAddress)
                .build();

        // Create order details dan calculate total
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtOrder(product.getPrice())  // Price snapshot
                    .subtotal(subtotal)
                    .build();

            orderDetails.add(detail);
            totalPrice = totalPrice.add(subtotal);

            // Reduce stock
            productService.reduceStock(product.getProductId(), cartItem.getQuantity());
        }

        order.setOrderDetails(orderDetails);
        order.setTotalPrice(totalPrice);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order
        cartRepository.deleteByUser_UserId(userId);

        return savedOrder;
    }

    /**
     * Update order status (admin function).
     */
    public Order updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * Cancel order (only if status is PENDING).
     */
    public Order cancelOrder(Integer orderId) {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Hanya order dengan status PENDING yang bisa dibatalkan");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Generate unique invoice code.
     * Format: INV-{HEX_TIMESTAMP}
     */
    private String generateInvoiceCode() {
        long timestamp = System.currentTimeMillis();
        return "INV-" + Long.toHexString(timestamp).toUpperCase();
    }
}
