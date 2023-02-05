package in.wynk.coupon.core.service.impl;

import in.wynk.coupon.core.constant.BeanConstant;
import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.coupon.core.service.ICouponCodeLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CouponCodeLinkServiceImpl implements ICouponCodeLinkService {

    @Autowired
    @Qualifier(BeanConstant.COUPON_MONGO_TEMPLATE_REF)
    private MongoTemplate mongoTemplate;

    @Override
    public void exhaustCouponCode(String couponCode) {
        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(couponCode)),
                new Update().inc("exhaustedCount", 1),
                CouponCodeLink.class);
    }

    @Override
    public CouponCodeLink fetchCouponCodeLink(String couponCode) {
        return mongoTemplate.findOne(new Query(new Criteria().andOperator(Criteria.where("_id").is(couponCode), Criteria.where("state").is("ACTIVE"))), CouponCodeLink.class);
    }
}
