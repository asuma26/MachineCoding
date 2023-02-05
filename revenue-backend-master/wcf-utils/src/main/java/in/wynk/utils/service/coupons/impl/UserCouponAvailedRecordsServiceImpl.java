package in.wynk.utils.service.coupons.impl;

import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.coupon.core.dao.repository.AvailedCouponsDao;
import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.coupons.IUserCouponAvailedRecordsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCouponAvailedRecordsServiceImpl implements IUserCouponAvailedRecordsService {

    private final AvailedCouponsDao availedCouponsDao;

    public UserCouponAvailedRecordsServiceImpl(AvailedCouponsDao availedCouponsDao) {
        this.availedCouponsDao = availedCouponsDao;
    }

    @Override
    public UserCouponAvailedRecord save(UserCouponAvailedRecord userCouponAvailedRecords) {
        return availedCouponsDao.save(userCouponAvailedRecords);
    }

    @Override
    public UserCouponAvailedRecord update(UserCouponAvailedRecord userCouponAvailedRecords) {
        UserCouponAvailedRecord codeLink = find(userCouponAvailedRecords.getId());
        return save(userCouponAvailedRecords);
    }

    @Override
    public void switchState(String id, State state) {
        UserCouponAvailedRecord userCouponAvailedRecord = find(id);
        userCouponAvailedRecord.setState(state);
        save(userCouponAvailedRecord);
    }

    @Override
    public UserCouponAvailedRecord find(String id) {
        return availedCouponsDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF010));
    }

    @Override
    public List<UserCouponAvailedRecord> findAll(Pageable pageable) {
        return availedCouponsDao.findAll(pageable).getContent();
    }

}
