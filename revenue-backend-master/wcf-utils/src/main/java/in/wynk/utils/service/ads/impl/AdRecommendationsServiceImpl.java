package in.wynk.utils.service.ads.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.core.dao.entity.mongo.AdRecommendations;
import in.wynk.targeting.core.dao.repository.mongo.AdRecommendationsMongoRepository;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.ads.IAdRecommendationsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdRecommendationsServiceImpl implements IAdRecommendationsService {

    private final AdRecommendationsMongoRepository adRecommendationsMongoRepository;

    public AdRecommendationsServiceImpl(AdRecommendationsMongoRepository adRecommendationsMongoRepository) {
        this.adRecommendationsMongoRepository = adRecommendationsMongoRepository;
    }

    @Override
    public AdRecommendations save(AdRecommendations adRecommendations) {
        return adRecommendationsMongoRepository.save(adRecommendations);
    }

    @Override
    public AdRecommendations update(AdRecommendations adRecommendations) {
        AdRecommendations recommendations = find(adRecommendations.getId());
        return save(adRecommendations);
    }

    @Override
    public AdRecommendations find(String id) {
        return adRecommendationsMongoRepository.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF003));
    }

    @Override
    public List<AdRecommendations> findAll(Pageable pageable) {
        return adRecommendationsMongoRepository.findAll(pageable).getContent();
    }

}
