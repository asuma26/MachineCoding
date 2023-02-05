package in.wynk.utils.service.subscription.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.repository.subscription.PlanDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.subscription.IPlansService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlansServiceImpl implements IPlansService {

    private final PlanDao plansDao;

    public PlansServiceImpl(PlanDao plansDao) {
        this.plansDao = plansDao;
    }

    @Override
    public Plan save(Plan plan) {
        return plansDao.save(plan);
    }

    @Override
    public Plan update(Plan plan) {
        Plan plan1 = find(plan.getId());
        return save(plan);
    }

    @Override
    public void switchState(Integer id, State state) {
        Plan plan = find(id);
        plan.setState(state);
        save(plan);
    }

    @Override
    public Plan find(Integer id) {
        return plansDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF017));
    }

    @Override
    public List<Plan> findAll(Pageable pageable) {
        return plansDao.findAll(pageable).getContent();
    }
}
