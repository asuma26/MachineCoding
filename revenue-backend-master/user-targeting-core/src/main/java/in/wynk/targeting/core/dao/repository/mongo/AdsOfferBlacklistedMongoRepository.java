package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.dao.entity.mongo.AdsOfferBlacklisted;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdsOfferBlacklistedMongoRepository extends MongoRepository<AdsOfferBlacklisted, String> {
    Optional<AdsOfferBlacklisted> findById(Integer id);
}
