package in.wynk.subscription.core.dao.repository.sedb;

import in.wynk.subscription.core.dao.entity.SubscriptionPack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPackDao extends MongoRepository<SubscriptionPack, Integer> {
}
