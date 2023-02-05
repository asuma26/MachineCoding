package in.wynk.utils.service.coupons;

import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.data.enums.State;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICouponsService {

    Coupon save(Coupon coupon);

    Coupon update(Coupon coupon);

    void switchState(String id, State state);

    Coupon find(String id);

    List<Coupon> findAll(Pageable pageable);

}
