package in.wynk.targeting.services;

import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import in.wynk.targeting.core.dao.repository.mongo.AdsConfigMongoRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author Abhishek
 * @created 18/05/20
 */
@Service
@NoArgsConstructor
public class AdConfigService {

    @Autowired
    private AdsConfigMongoRepository adsConfigRepository;

    @Autowired
    private MongoTemplate adsMongoTemplate;

    @Cacheable(cacheName = "AD_CONFIG", cacheKey = "T(java.lang.String).format('%s:%s:%s', #root.methodName, #adState.state, #adTypes)", cacheManager = BeanConstant.L1CACHE_MANAGER, l1CacheTtl = 60 * 60)
    public List<AdConfig> findAllByStateAndTypeIn(AdState adState, Collection<AdType> adTypes) {
        return adsConfigRepository.findAllByStateAndTypeIn(adState, adTypes);
    }

    @Cacheable(cacheName = "AD_CONFIG", cacheKey = "T(java.lang.String).format('%s:%s:%s', #root.methodName, #adState.state, #clientId)", cacheManager = BeanConstant.L1CACHE_MANAGER, l1CacheTtl = 20 * 60)
    public List<AdConfig> findAllByStateAndByClientIdAndTypeIn(AdState adState, String clientId, Collection<AdType> adTypes) {
        return adsConfigRepository.findAllByStateAndClientIdAndTypeIn(adState, clientId, adTypes);
    }

}
