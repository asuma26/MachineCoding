package in.wynk.utils.service.testingConfig.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import in.wynk.targeting.core.dao.repository.mongo.AdsTestUserRepository;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.testingConfig.IAdConfigTestUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdConfigTestUserServiceImpl implements IAdConfigTestUserService {

    private final AdsTestUserRepository adConfigTestUserDao;

    public AdConfigTestUserServiceImpl(AdsTestUserRepository adConfigTestUserDao) {
        this.adConfigTestUserDao = adConfigTestUserDao;
    }

    @Override
    public AdsConfigTestUser save(AdsConfigTestUser adsConfigTestUser) {
        return adConfigTestUserDao.save(adsConfigTestUser);
    }

    @Override
    public AdsConfigTestUser update(AdsConfigTestUser adsConfigTestUser) {
        AdsConfigTestUser adsConfigTestUser1 = find(adsConfigTestUser.getId());
        return save(adsConfigTestUser);
    }

    @Override
    public void switchState(String id, State state) {
        AdsConfigTestUser adsConfigTestUser = find(id);
        adsConfigTestUser.setState(state);
        save(adsConfigTestUser);
    }

    @Override
    public AdsConfigTestUser find(String id) {
        return adConfigTestUserDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF005));
    }

    @Override
    public List<AdsConfigTestUser> findAll(Pageable pageable) {
        return adConfigTestUserDao.findAll(pageable).getContent();
    }
}
