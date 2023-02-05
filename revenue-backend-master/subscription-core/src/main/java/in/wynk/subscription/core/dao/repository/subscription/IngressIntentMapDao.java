package in.wynk.subscription.core.dao.repository.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.IngressIntentMap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngressIntentMapDao extends MongoRepository<IngressIntentMap, String> {
    List<IngressIntentMap> findAllByState(State state);
}
