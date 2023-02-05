package in.wynk.coupon.core.dao.entity;

import in.wynk.coupon.core.constant.CouponProvisionState;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponPair {

    @Field("coupon_code")
    private String code;
    @Setter
    @Field("coupon_entry_timestamp")
    private long timestamp;
    @Setter
    @Field("coupon_provision_state")
    private CouponProvisionState provisionState;

}
