package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.dao.entity.mongo.WynkClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WynkClientMongoRepository extends MongoRepository<WynkClient, String> {

    Optional<WynkClient> getByClientIdAndOs(String ClientId, String os);

}
