package in.wynk.coupon.core.dto;

import in.wynk.coupon.core.constant.ProvisionSource;
import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import in.wynk.subscription.common.dto.PlanDTO;
import in.wynk.subscription.common.dto.UserProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Builder
public class CouponContext {

    private final String uid;
    private final String msisdn;
    private final String itemId;
    private final String service;
    private final String paymentCode;
    private final ProvisionSource source;

    private final PlanDTO plan;

    @Setter
    private Coupon coupon;
    @Setter
    private CouponCodeLink couponCodeLink;

    @Setter
    private UserCouponAvailedRecord couponAvailedRecord;
    @Setter
    private List<UserCouponWhiteListRecord> couponWhiteListRecords;
    private final UserProfile userProfile = new UserProfile();

}
