package ogami_api.ogani_website.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk create order request (checkout).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank(message = "Receiver name wajib diisi")
    private String receiverName;

    @NotBlank(message = "Receiver phone wajib diisi")
    private String receiverPhone;

    @NotBlank(message = "Shipping address wajib diisi")
    private String shippingAddress;
}
