package in.wynk.coupon.core.dao.repository;

import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.data.enums.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponDao extends MongoRepository<Coupon, String> {

    List<Coupon> findCouponByState(State state);

}
