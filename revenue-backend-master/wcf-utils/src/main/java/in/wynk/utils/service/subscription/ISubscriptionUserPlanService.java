package in.wynk.utils.service.subscription;

import in.wynk.utils.response.SubscriptionAndUserPlanResponse;

public interface ISubscriptionUserPlanService {
    SubscriptionAndUserPlanResponse populateResponse(String uid,String service);
}
