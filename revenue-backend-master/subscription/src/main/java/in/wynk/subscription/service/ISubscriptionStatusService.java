package in.wynk.subscription.service;

import in.wynk.subscription.dto.SubscriptionStatus;

import java.util.List;

public interface ISubscriptionStatusService {

    List<SubscriptionStatus> getSubscriptionStatus(String uid, String service);

}
