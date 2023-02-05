package in.wynk.subscription.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionUnProvisionRequest {

    private final int planId;
    private final String uid;
    private final String service;

}
