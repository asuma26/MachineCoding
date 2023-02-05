package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.dao.entity.mongo.AdClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdClientsMongoRepository extends MongoRepository<AdClient, String> {

    Optional<AdClient> findByClientId(String id);

}
