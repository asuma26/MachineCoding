package in.wynk.targeting.core.dao.repository.cassandra;

import in.wynk.targeting.core.dao.entity.cassandra.AdTargeting;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdTargetingRepository extends CassandraRepository<AdTargeting, String> {

    List<AdTargeting> findByUid(String uid);
}
