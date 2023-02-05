package in.wynk.subscription.core.dao.repository.usermeta;

import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Abhishek
 * @created 19/06/20
 */
@Repository
public interface ThanksSegmentRepository extends CassandraRepository<ThanksUserSegment, String>, ThanksSegmentRepositoryCustom {

    List<ThanksUserSegment> findBySi(String si);

    List<ThanksUserSegment> findAllBySiIn(Set<String> si);
}