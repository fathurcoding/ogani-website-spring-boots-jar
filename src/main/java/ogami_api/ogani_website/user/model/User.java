package ogami_api.ogani_website.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ogami_api.ogani_website.cart.model.Cart;
import ogami_api.ogani_website.order.model.Order;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity class untuk tabel users.
 * Represents user dalam sistem (admin atau customer).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cartItems", "orders"})
@EqualsAndHashCode(exclude = {"cartItems", "orders"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // ===== Enhanced Profile Fields =====
    
    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "age")
    private Integer age;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private UserRole role = UserRole.CUSTOMER;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> cartItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
