package in.wynk.subscription.service;

import in.wynk.common.dto.SessionRequest;
import in.wynk.common.dto.SessionResponse;

public interface ISubscriptionSessionService {

    SessionResponse purchasePlans(SessionRequest request);

    SessionResponse managePlans(SessionRequest request);

}
