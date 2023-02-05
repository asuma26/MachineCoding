package in.wynk.subscription.core.dao.repository.subscription;


import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDao extends MongoRepository<Plan, Integer> {

    Page<Plan> findAllByState(State state, Pageable pageable);

    List<Plan> findAllByState(State state);
}
