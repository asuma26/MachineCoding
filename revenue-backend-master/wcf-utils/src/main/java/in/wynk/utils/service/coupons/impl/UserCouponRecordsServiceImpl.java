package in.wynk.utils.service.coupons.impl;

import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import in.wynk.coupon.core.dao.repository.WhitelistedCouponDao;
import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.coupons.IUserCouponRecordsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCouponRecordsServiceImpl implements IUserCouponRecordsService {

    private final WhitelistedCouponDao whitelistedCouponDao;

    public UserCouponRecordsServiceImpl(WhitelistedCouponDao whitelistedCouponDao) {
        this.whitelistedCouponDao = whitelistedCouponDao;
    }

    @Override
    public UserCouponWhiteListRecord save(UserCouponWhiteListRecord userCouponWhiteListRecord) {
        return whitelistedCouponDao.save(userCouponWhiteListRecord);
    }

    @Override
    public UserCouponWhiteListRecord update(UserCouponWhiteListRecord userCouponWhiteListRecord) {
        UserCouponWhiteListRecord userCouponWhiteListRecord1 = find(userCouponWhiteListRecord.getId());
        return save(userCouponWhiteListRecord);
    }

    @Override
    public void switchState(String id, State state) {
        UserCouponWhiteListRecord userCouponWhiteListRecord = find(id);
        userCouponWhiteListRecord.setState(state);
        save(userCouponWhiteListRecord);
    }

    @Override
    public UserCouponWhiteListRecord find(String id) {
        return whitelistedCouponDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF011));
    }

    @Override
    public List<UserCouponWhiteListRecord> findAll(Pageable pageable) {
        return whitelistedCouponDao.findAll(pageable).getContent();
    }
}
