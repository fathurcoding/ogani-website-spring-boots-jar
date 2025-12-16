package ogami_api.ogani_website.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO untuk create/update Product request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name wajib diisi")
    private String productName;

    private String description;

    @NotNull(message = "Price wajib diisi")
    @Min(value = 0, message = "Price tidak boleh negatif")
    private BigDecimal price;

    @Min(value = 0, message = "Stock tidak boleh negatif")
    private Integer stock;

    private String productImage;

    @NotNull(message = "Category ID wajib diisi")
    private Integer categoryId;
}
