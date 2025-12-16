package ogami_api.ogani_website.order.repository;

import ogami_api.ogani_website.order.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk OrderDetail entity.
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    /**
     * Find all details for a specific order.
     */
    List<OrderDetail> findByOrder_OrderId(Integer orderId);
}
