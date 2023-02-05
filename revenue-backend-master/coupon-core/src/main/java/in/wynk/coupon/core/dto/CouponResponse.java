package in.wynk.coupon.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.coupon.core.constant.CouponProvisionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {

    private CouponDTO coupon;
    @JsonIgnore
    private CouponError error;
    private CouponProvisionState state;

}
