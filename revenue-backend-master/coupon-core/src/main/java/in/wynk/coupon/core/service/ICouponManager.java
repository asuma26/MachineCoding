package in.wynk.coupon.core.service;

import in.wynk.coupon.core.dto.CouponDTO;
import in.wynk.coupon.core.dto.CouponProvisionRequest;
import in.wynk.coupon.core.dto.CouponResponse;

import java.util.List;

public interface ICouponManager {

    List<CouponDTO> getEligibleCoupons(CouponProvisionRequest request);

    CouponResponse evalCouponEligibility(CouponProvisionRequest request);

    CouponResponse applyCoupon(CouponProvisionRequest request);

    CouponResponse removeCoupon(String uid, String couponCode);

    void exhaustCoupon(String uid, String couponCode);

}
