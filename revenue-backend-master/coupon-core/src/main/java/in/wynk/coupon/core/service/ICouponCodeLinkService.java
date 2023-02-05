package in.wynk.coupon.core.service;

import in.wynk.coupon.core.dao.entity.CouponCodeLink;

public interface ICouponCodeLinkService {

    void exhaustCouponCode(String couponCode);

    CouponCodeLink fetchCouponCodeLink(String couponCode);

}
