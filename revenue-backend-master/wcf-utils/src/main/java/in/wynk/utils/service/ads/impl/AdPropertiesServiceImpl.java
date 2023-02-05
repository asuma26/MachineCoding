package in.wynk.utils.service.ads.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.core.dao.entity.mongo.AdProperties;
import in.wynk.targeting.core.dao.repository.mongo.AdPropertiesMongoRepository;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.ads.IAdPropertiesService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdPropertiesServiceImpl implements IAdPropertiesService {

    private final AdPropertiesMongoRepository adPropertiesMongoRepository;

    public AdPropertiesServiceImpl(AdPropertiesMongoRepository adPropertiesMongoRepository) {
        this.adPropertiesMongoRepository = adPropertiesMongoRepository;
    }

    @Override
    public AdProperties save(AdProperties adProperties) {
        return adPropertiesMongoRepository.save(adProperties);
    }

    @Override
    public AdProperties update(AdProperties adProperties) {
        AdProperties properties = find(adProperties.getId());
        return save(adProperties);
    }

    @Override
    public AdProperties find(String id) {
        return adPropertiesMongoRepository.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF002));
    }

    @Override
    public List<AdProperties> findAll(Pageable pageable) {
        return adPropertiesMongoRepository.findAll(pageable).getContent();
    }
}
