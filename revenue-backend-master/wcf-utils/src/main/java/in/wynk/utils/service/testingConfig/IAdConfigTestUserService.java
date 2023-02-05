package in.wynk.utils.service.testingConfig;

import in.wynk.data.enums.State;
import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdConfigTestUserService {

    AdsConfigTestUser save(AdsConfigTestUser adsConfigTestUser);

    AdsConfigTestUser update(AdsConfigTestUser adsConfigTestUser);

    void switchState(String id, State state);

    AdsConfigTestUser find(String id);

    List<AdsConfigTestUser> findAll(Pageable pageable);

}
