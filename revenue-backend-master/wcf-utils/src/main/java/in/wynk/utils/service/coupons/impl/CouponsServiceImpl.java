package in.wynk.utils.service.coupons.impl;

import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.coupon.core.dao.repository.CouponDao;
import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.coupons.ICouponsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponsServiceImpl implements ICouponsService {

    private final CouponDao couponDao;

    public CouponsServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponDao.save(coupon);
    }

    @Override
    public Coupon update(Coupon coupon) {
        Coupon codeLink = find(coupon.getId());
        return save(coupon);
    }

    @Override
    public void switchState(String id, State state) {
        Coupon coupon = find(id);
        coupon.setState(state);
        save(coupon);
    }

    @Override
    public Coupon find(String id) {
        return couponDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF009));
    }

    @Override
    public List<Coupon> findAll(Pageable pageable) {
        return couponDao.findAll(pageable).getContent();
    }

}
