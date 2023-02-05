package in.wynk.coupon.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "user_coupon_records")
public class UserCouponWhiteListRecord extends MongoBaseEntity {

    private String uid;
    @Field("coupon_id")
    private String couponId;

}
