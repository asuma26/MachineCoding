package in.wynk.subscription.core.dao.repository.userdata;

import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

/**
 * @author Abhishek
 * @created 28/08/20
 */
public interface UserPlanDetailsRepository extends CassandraRepository<UserPlanDetails, String> {

    List<UserPlanDetails> findByServiceAndUid(String service, String uid);
}
