package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsTestUserRepository extends MongoRepository<AdsConfigTestUser, String> {
}