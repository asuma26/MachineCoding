package in.wynk.order.core.dao.repository;

import in.wynk.order.core.dao.entity.OrderErrorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrderErrorDao extends JpaRepository<OrderErrorDetails, String> {

    Optional<OrderErrorDetails> findById(@Param("orderId") String id);


}
