package ogami_api.ogani_website.order.repository;

import ogami_api.ogani_website.order.model.Order;
import ogami_api.ogani_website.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Find all orders for a specific user.
     */
    List<Order> findByUser_UserId(Integer userId);

    /**
     * Find order by invoice code.
     */
    Optional<Order> findByInvoiceCode(String invoiceCode);

    /**
     * Find orders by status.
     */
    List<Order> findByOrderStatus(OrderStatus status);
    
    /**
     * Find orders by user and status.
     */
    List<Order> findByUser_UserIdAndOrderStatus(Integer userId, OrderStatus status);
}
