package in.wynk.utils.service.ads.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import in.wynk.targeting.core.dao.repository.mongo.AdsConfigMongoRepository;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.ads.IAdConfigService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdConfigServiceImpl implements IAdConfigService {

    private final AdsConfigMongoRepository adsConfigMongoRepository;

    public AdConfigServiceImpl(AdsConfigMongoRepository adsConfigMongoRepository) {
        this.adsConfigMongoRepository = adsConfigMongoRepository;
    }

    @Override
    public AdConfig save(AdConfig adConfig) {
        return adsConfigMongoRepository.save(adConfig);
    }

    @Override
    public AdConfig update(AdConfig adConfig) {
        AdConfig config = find(adConfig.getId());
        return save(adConfig);
    }

    @Override
    public void switchState(String id, AdState adState) {
        AdConfig config = find(id);
        config.setState(adState);
        save(config);
    }

    @Override
    public AdConfig find(String id) {
        return adsConfigMongoRepository.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF004));
    }

    @Override
    public List<AdConfig> findAll(Pageable pageable) {
        return adsConfigMongoRepository.findAll(pageable).getContent();
    }

    @Override
    public void switchAll(Map<String, String> payload) {
        payload.forEach((id, state) -> switchState(id, AdState.valueOf(state)));
    }
}
