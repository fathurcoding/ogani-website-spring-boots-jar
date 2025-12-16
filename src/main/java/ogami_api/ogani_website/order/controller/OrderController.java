package ogami_api.ogani_website.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.order.dto.OrderItemResponse;
import ogami_api.ogani_website.order.dto.OrderRequest;
import ogami_api.ogani_website.order.dto.OrderResponse;
import ogami_api.ogani_website.order.model.Order;
import ogami_api.ogani_website.order.model.OrderStatus;
import ogami_api.ogani_website.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Order API.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /api/orders - Get current user's orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders() {
        Integer userId = 1;  // TODO: Get from JWT authentication

        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderResponse> response = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{id} - Get order detail.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    /**
     * GET /api/orders/invoice/{code} - Get order by invoice code.
     */
    @GetMapping("/invoice/{code}")
    public ResponseEntity<OrderResponse> getOrderByInvoice(@PathVariable String code) {
        Order order = orderService.getOrderByInvoiceCode(code);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    /**
     * POST /api/orders - Create order (checkout from cart).
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Integer userId = 1;  // TODO: Get from JWT authentication

        Order order = orderService.createOrderFromCart(
                userId,
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getShippingAddress()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toOrderResponse(order));
    }

    /**
     * PUT /api/orders/{id}/status - Update order status (Admin only).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    /**
     * DELETE /api/orders/{id} - Cancel order (only if PENDING).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Integer id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    // Helper methods

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = null;
        
        if (order.getOrderDetails() != null) {
            items = order.getOrderDetails().stream()
                    .map(detail -> OrderItemResponse.builder()
                            .detailId(detail.getDetailId())
                            .productId(detail.getProduct().getProductId())
                            .productName(detail.getProduct().getProductName())
                            .quantity(detail.getQuantity())
                            .priceAtOrder(detail.getPriceAtOrder())
                            .subtotal(detail.getSubtotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .invoiceCode(order.getInvoiceCode())
                .orderStatus(order.getOrderStatus())
                .orderTime(order.getOrderTime())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .totalPrice(order.getTotalPrice())
                .items(items)
                .build();
    }
}
