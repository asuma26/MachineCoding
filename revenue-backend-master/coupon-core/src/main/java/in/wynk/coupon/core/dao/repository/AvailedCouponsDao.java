package in.wynk.coupon.core.dao.repository;

import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailedCouponsDao extends MongoRepository<UserCouponAvailedRecord, String> {
}
