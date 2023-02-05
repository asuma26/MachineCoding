package in.wynk.targeting.services;

import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdProperties;
import in.wynk.targeting.core.dao.repository.mongo.AdPropertiesMongoRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class AdPropertiesService {
    @Autowired
    private AdPropertiesMongoRepository adPropertiesMongoRepository;

    @Cacheable(cacheName = "AD_PROPS", cacheKey = "T(java.lang.String).format('%s:%s', #root.methodName, #adType.name())", cacheManager = BeanConstant.L1CACHE_MANAGER, l1CacheTtl = 60 * 60)
    public AdProperties findByAdType(AdType adType) {
        return adPropertiesMongoRepository.findByType(adType);
    }
}
