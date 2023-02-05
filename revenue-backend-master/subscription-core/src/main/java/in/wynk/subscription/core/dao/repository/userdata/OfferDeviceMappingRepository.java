package in.wynk.subscription.core.dao.repository.userdata;

import in.wynk.subscription.core.dao.entity.OfferDeviceMapping;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Abhishek
 * @created 20/06/20
 */
@Repository
public interface OfferDeviceMappingRepository extends CassandraRepository<OfferDeviceMapping, String> {
    List<OfferDeviceMapping> findByServiceAndDeviceId(String service, String deviceId);
}
