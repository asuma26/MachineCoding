package in.wynk.subscription.service;

import in.wynk.advice.TimeIt;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.dto.OfferPlanProductsForProvision;
import in.wynk.subscription.dto.request.ProductsComputationRequest;
import in.wynk.subscription.dto.response.ProductsComputationResponse;

import java.util.List;
import java.util.Set;

public interface IUserProductService {

    @Deprecated
    OfferPlanProductsForProvision getProductToBeProvisioned(Set<Plan> plans, List<Subscription> activeSubscriptions, List<UserPlanDetails> activePlans);

    @TimeIt
    <T extends ProductsComputationResponse, R extends ProductsComputationRequest> T compute(R request);

}
