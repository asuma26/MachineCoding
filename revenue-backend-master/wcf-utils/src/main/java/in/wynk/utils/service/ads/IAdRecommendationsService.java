package in.wynk.utils.service.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdRecommendations;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdRecommendationsService {

    AdRecommendations save(AdRecommendations adRecommendations);

    AdRecommendations update(AdRecommendations adRecommendations);

    AdRecommendations find(String id);

    List<AdRecommendations> findAll(Pageable pageable);

}
