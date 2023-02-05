package in.wynk.coupon.core.dao.repository;

import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhitelistedCouponDao extends MongoRepository<UserCouponWhiteListRecord, String> {

    List<UserCouponWhiteListRecord> findByUid(String uid);

}
