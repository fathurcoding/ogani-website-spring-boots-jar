package ogami_api.ogani_website.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO untuk bulk create categories.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkCategoryRequest {

    @NotEmpty(message = "Categories list cannot be empty")
    @Size(max = 100, message = "Cannot create more than 100 categories at once")
    @Valid
    private List<CategoryRequest> categories;
}
