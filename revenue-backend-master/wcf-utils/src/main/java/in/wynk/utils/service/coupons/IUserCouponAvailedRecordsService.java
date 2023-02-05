package in.wynk.utils.service.coupons;

import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.data.enums.State;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserCouponAvailedRecordsService {

    UserCouponAvailedRecord save(UserCouponAvailedRecord userCouponAvailedRecord);

    UserCouponAvailedRecord update(UserCouponAvailedRecord userCouponAvailedRecord);

    void switchState(String id, State state);

    UserCouponAvailedRecord find(String id);

    List<UserCouponAvailedRecord> findAll(Pageable pageable);

}
