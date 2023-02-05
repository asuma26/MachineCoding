package in.wynk.ut.base.dao;

import in.wynk.ut.base.model.UserTarget;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserTargetRepo extends ReactiveCassandraRepository<UserTarget, String> {

    Flux<UserTarget> findByUid(final String uid);

    Flux<UserTarget> findByAdid(final String adid);

    Mono<UserTarget> findOneByUid(final String uid);

    Mono<UserTarget> findOneByAdid(final String adid);

}
