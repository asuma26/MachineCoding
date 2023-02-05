package in.wynk.coupon.core.dto;

import in.wynk.coupon.core.constant.ProvisionSource;
import in.wynk.subscription.common.dto.PlanDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponProvisionRequest {

    private final String uid;
    private final String msisdn;
    private final String itemId;
    private final String service;
    private final String couponCode;
    private final String paymentCode;
    private final PlanDTO selectedPlan;
    private final ProvisionSource source;

}
