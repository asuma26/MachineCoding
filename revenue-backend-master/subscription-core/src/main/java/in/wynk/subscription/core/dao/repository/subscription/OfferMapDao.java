package in.wynk.subscription.core.dao.repository.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.OfferMap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferMapDao extends MongoRepository<OfferMap, String> {

    List<OfferMap> findAllByState(State state);

}
