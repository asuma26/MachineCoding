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
@Document(collection = "coupons")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends MongoBaseEntity {

    @Field("coupon_title")
    private String title;
    @Field("coupon_description")
    private String description;
    @Field("coupon_rule_expression")
    private String ruleExpression;
    @Field("coupon_expiry")
    private long expiry;
    @Field("coupon_discount_percent")
    private double discountPercent;

}
