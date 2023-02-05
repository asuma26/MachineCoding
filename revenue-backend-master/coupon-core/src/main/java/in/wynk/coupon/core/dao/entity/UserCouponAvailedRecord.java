package in.wynk.coupon.core.dao.entity;

import in.wynk.coupon.core.constant.CouponProvisionState;
import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "user_coupon_availed_records")
public class UserCouponAvailedRecord extends MongoBaseEntity {

    @Field("coupon_pairs")
    private List<CouponPair> couponPairs;

    /*
     *  Remove old applied state coupon pair, if user try to apply new coupon
     */
    public void beforeCouponApplied() {
        couponPairs = couponPairs.stream().filter(couponPair -> couponPair.getProvisionState() == CouponProvisionState.EXHAUSTED).collect(Collectors.toList());
    }

}
