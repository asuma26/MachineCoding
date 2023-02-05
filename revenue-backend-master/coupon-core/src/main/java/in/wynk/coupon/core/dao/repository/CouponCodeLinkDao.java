package in.wynk.coupon.core.dao.repository;

import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponCodeLinkDao extends MongoRepository<CouponCodeLink, String> {
}
