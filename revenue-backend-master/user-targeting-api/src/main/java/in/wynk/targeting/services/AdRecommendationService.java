package in.wynk.targeting.services;

import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdRecommendation;
import in.wynk.targeting.core.dao.entity.mongo.AdRecommendations;
import in.wynk.targeting.core.dao.repository.mongo.AdRecommendationsMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdRecommendationService {

    private final AdRecommendationsMongoRepository adRecommendationsRepository;

    public AdRecommendationService(AdRecommendationsMongoRepository adRecommendationsRepository) {
        this.adRecommendationsRepository = adRecommendationsRepository;
    }

    public List<AdRecommendation> getAdRecommendation(AdType adType, List<String> adIds) {
        AdRecommendations adRecommendations = getAdRecommendations(adType);
        return adRecommendations.getRecommendations()
                .stream()
                .filter(AdRecommendation::isRecommended)
                .filter(adRecommendation -> adIds.contains(adRecommendation.getSlotId()))
                .collect(Collectors.toList());
    }

    @Cacheable(cacheName = "AD_RECOMMENDATION", cacheKey = "T(java.lang.String).format('%s:%s', #root.methodName, #adType.name())", cacheManager = BeanConstant.L1CACHE_MANAGER, l1CacheTtl = 60 * 60)
    private AdRecommendations getAdRecommendations(AdType adType){
        return adRecommendationsRepository.findByType(adType);
    }

}
