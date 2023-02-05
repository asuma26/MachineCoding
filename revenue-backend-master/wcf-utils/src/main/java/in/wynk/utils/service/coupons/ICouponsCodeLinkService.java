package in.wynk.utils.service.coupons;

import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.data.enums.State;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICouponsCodeLinkService {

    List<CouponCodeLink> generate(Integer n, String couponId, String totalCount);

    CouponCodeLink save(CouponCodeLink couponCodeLink);

    CouponCodeLink update(CouponCodeLink couponCodeLink);

    void switchState(String id, State state);

    CouponCodeLink find(String id);

    List<CouponCodeLink> findAll(Pageable pageable);

}
