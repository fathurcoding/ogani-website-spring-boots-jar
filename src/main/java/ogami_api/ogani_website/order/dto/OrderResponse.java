package ogami_api.ogani_website.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ogami_api.ogani_website.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO untuk order detail response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Integer orderId;
    private String invoiceCode;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}
