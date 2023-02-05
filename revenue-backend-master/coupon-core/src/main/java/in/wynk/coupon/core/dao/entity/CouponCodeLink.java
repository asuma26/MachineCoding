package in.wynk.coupon.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@SuperBuilder
@Document(collection = "coupon_code_link")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCodeLink extends MongoBaseEntity {

    @Field("coupon_id")
    private String couponId;
    @Field("coupon_exhausted_count")
    private long exhaustedCount;
    @Field("coupon_total_count")
    private long totalCount;

}
