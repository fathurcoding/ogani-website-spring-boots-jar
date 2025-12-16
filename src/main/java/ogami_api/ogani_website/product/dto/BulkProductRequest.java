package ogami_api.ogani_website.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO untuk bulk create products.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkProductRequest {

    @NotEmpty(message = "Products list cannot be empty")
    @Size(max = 100, message = "Cannot create more than 100 products at once")
    @Valid
    private List<ProductRequest> products;
}
