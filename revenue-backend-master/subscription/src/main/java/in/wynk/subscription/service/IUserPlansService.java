package in.wynk.subscription.service;

import in.wynk.common.dto.SessionDTO;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.subscription.common.dto.ActivePlanDetails;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.dto.response.UserActiveBenefits;
import in.wynk.subscription.dto.response.UserCombinedBenefits;
import in.wynk.subscription.dto.response.UserEligibleBenefits;

import java.util.Map;
import java.util.Set;

public interface IUserPlansService {

    Map<UserPlanDetails, Set<Product>> getActiveUserPlanProductDetails(String uid, String service);

    UserActiveBenefits getUserActiveBenefits(SessionDTO sessionDTO);

    ActivePlanDetails getActivePlanDetails(String uid, int planId);

    UserActivePlansResponse getActivePlansForUser(String uid, String service);

    UserEligibleBenefits.UserEligibleBenefitsData getUserEligiblePlans(SessionDTO sessionDTO);

    UserCombinedBenefits getUserCombinedBenefits(SessionDTO sessionDTO);

}
