package in.wynk.order.core.dao.repository;

import in.wynk.order.core.dao.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrderDao extends JpaRepository<Order, String> {

    Optional<Order> findById(@Param("orderId") String id);

    Optional<Order> findByPartnerOrderId(@Param("partnerOrderId") String pid);

}
