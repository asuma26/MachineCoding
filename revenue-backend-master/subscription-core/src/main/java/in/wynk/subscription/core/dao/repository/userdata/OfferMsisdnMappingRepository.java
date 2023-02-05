package in.wynk.subscription.core.dao.repository.userdata;

import in.wynk.subscription.core.dao.entity.OfferMsisdnMapping;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Abhishek
 * @created 20/06/20
 */
@Deprecated
@Repository
public interface OfferMsisdnMappingRepository extends CassandraRepository<OfferMsisdnMapping, String> {
    List<OfferMsisdnMapping> findByServiceAndMsisdn(String service, String msisdn);
}
