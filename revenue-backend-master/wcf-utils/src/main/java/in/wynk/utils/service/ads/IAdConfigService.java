package in.wynk.utils.service.ads;

import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IAdConfigService {

    AdConfig save(AdConfig adConfig);

    AdConfig update(AdConfig adConfig);

    void switchState(String id, AdState adState);

    AdConfig find(String id);

    List<AdConfig> findAll(Pageable pageable);

    void switchAll(Map<String, String> payload);
}
