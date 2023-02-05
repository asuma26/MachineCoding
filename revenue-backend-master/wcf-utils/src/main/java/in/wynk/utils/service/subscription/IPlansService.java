package in.wynk.utils.service.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Plan;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPlansService {

    Plan save(Plan plan);

    Plan update(Plan plan);

    void switchState(Integer id, State state);

    Plan find(Integer id);

    List<Plan> findAll(Pageable pageable);

}
