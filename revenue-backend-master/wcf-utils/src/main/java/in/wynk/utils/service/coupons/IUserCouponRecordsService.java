package in.wynk.utils.service.coupons;

import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import in.wynk.data.enums.State;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserCouponRecordsService {

    UserCouponWhiteListRecord save(UserCouponWhiteListRecord userCouponWhiteListRecord);

    UserCouponWhiteListRecord update(UserCouponWhiteListRecord userCouponWhiteListRecord);

    void switchState(String id, State state);

    UserCouponWhiteListRecord find(String id);

    List<UserCouponWhiteListRecord> findAll(Pageable pageable);

}
