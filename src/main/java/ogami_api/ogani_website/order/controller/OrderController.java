package ogami_api.ogani_website.order.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ogami_api.ogani_website.order.dto.OrderItemResponse;
import ogami_api.ogani_website.order.dto.OrderRequest;
import ogami_api.ogani_website.order.dto.OrderResponse;
import ogami_api.ogani_website.order.model.Order;
import ogami_api.ogani_website.order.model.OrderStatus;
import ogami_api.ogani_website.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller untuk Order API.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management and checkout (authentication required)")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /api/orders - Get current user's orders with optional pagination.
     * Params: page, size, sort
     */
    @GetMapping
    public ResponseEntity<?> getUserOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuth(authentication);

        // If pagination params provided, return paginated response
        if (page != null && size != null) {
            Pageable pageable = createPageable(page, size, sort);
            Page<Order> orderPage = orderService.getOrdersByUserId(userId, pageable);
            Page<OrderResponse> responsePage = orderPage.map(this::toOrderResponse);
            return ResponseEntity.ok(responsePage);
        }
        
        // Otherwise return all orders
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

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);

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
    @PreAuthorize("hasRole('ADMIN')")

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
    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Integer id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(toOrderResponse(order));
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

    private Pageable createPageable(Integer page, Integer size, String sort) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 10;
        
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])) 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, field));
        }
        
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "orderTime"));
    }

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
