package in.wynk.subscription.service;

import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import in.wynk.subscription.common.dto.ProvisioningResponse;

public interface IUserPlanManager {

    ProvisioningResponse subscribe(PlanProvisioningRequest request);

    PlanProvisioningResponse unsubscribe(PlanUnProvisioningRequest request);

}
