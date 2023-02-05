package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdRecommendations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRecommendationsMongoRepository extends MongoRepository<AdRecommendations, String> {

    AdRecommendations findByType(AdType adType);

}
