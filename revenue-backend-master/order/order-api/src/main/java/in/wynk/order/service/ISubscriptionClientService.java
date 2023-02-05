package in.wynk.order.service;

import in.wynk.subscription.common.dto.ActivePlanDetails;
import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;

public interface ISubscriptionClientService {

    PlanProvisioningResponse subscribe(PlanProvisioningRequest request);

    PlanProvisioningResponse unsubscribe(PlanUnProvisioningRequest request);

    ActivePlanDetails getActivePlan(String uid, int planId);


}
