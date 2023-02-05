package in.wynk.subscription.core.dao.repository.subscription;


import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferDao extends MongoRepository<Offer, Integer> {

    Page<Offer> findAllByState(State state, Pageable pageable);

    List<Offer> findAllByState(State state);
}
