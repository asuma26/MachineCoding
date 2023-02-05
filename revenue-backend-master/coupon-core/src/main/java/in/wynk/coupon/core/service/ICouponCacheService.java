package in.wynk.coupon.core.service;

import in.wynk.coupon.core.dao.entity.Coupon;

import java.util.Collection;

public interface ICouponCacheService {

    Collection<Coupon> getAllCoupons();

    Collection<String> getAllCouponIds();

    Coupon getCouponById(String id);

}
