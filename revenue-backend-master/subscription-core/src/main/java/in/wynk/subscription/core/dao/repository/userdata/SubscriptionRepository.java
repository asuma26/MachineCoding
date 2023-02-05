package in.wynk.subscription.core.dao.repository.userdata;

import in.wynk.subscription.core.dao.entity.Subscription;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Abhishek
 * @created 19/06/20
 */
@Repository
@Deprecated
public interface SubscriptionRepository extends CassandraRepository<Subscription, String> {
    List<Subscription> findByUidAndService(String uid, String service);
}
