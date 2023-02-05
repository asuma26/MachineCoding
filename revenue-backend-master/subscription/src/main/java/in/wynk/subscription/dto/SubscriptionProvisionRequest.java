package in.wynk.subscription.dto;

import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Deprecated
public class SubscriptionProvisionRequest {

    private final String uid;
    private final String msisdn;
    private final String service;
    private final String referenceId;
    private final String paymentMethod;
    private final boolean autoRenew;
    private final List<Subscription> activeSubscriptions;
    private final OfferPlanProductsForProvision offerPlanProductsForProvision;
    private final List<UserPlanDetails> activePlanDetails;

}
