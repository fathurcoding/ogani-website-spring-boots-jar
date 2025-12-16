package ogami_api.ogani_website.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.order.model.OrderDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity class untuk tabel products.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"category", "cartItems", "orderDetails"})
@EqualsAndHashCode(exclude = {"category", "cartItems", "orderDetails"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "product_image", length = 255)
    private String productImage;

    // Relationship to Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Relationship to Cart
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Cart> cartItems;

    // Relationship to OrderDetail
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
}
