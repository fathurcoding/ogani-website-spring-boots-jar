package ogami_api.ogani_website.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO untuk Product response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String productImage;
    private Integer categoryId;
    private String categoryName;
}
