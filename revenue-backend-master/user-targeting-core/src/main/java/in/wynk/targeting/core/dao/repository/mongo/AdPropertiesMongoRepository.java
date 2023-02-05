package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdProperties;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdPropertiesMongoRepository extends MongoRepository<AdProperties, String> {
    AdProperties findByType(AdType adType);
}
