package in.wynk.subscription.service;

import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.dto.SubscriptionProvisionRequest;
import in.wynk.subscription.dto.SubscriptionUnProvisionRequest;

import java.util.List;

public interface ISubscriptionService {

    @Deprecated
    List<Subscription> provision(SubscriptionProvisionRequest request);

    @Deprecated
    void unProvision(SubscriptionUnProvisionRequest request);

}
