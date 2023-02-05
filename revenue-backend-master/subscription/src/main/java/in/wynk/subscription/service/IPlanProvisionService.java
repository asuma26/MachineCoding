package in.wynk.subscription.service;

import in.wynk.common.enums.AppId;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;

import java.util.List;

public interface IPlanProvisionService {
    List<Plan> getEligiblePlans(int offerId, AppId appId, List<UserPlanDetails> userPlanDetailsList);
}
