package in.wynk.subscription.service;

import in.wynk.subscription.dto.request.AirtelEventRequest;

public interface IAirtelEventService {
    void saveMsisdnToCollection(AirtelEventRequest airtelEventRequest);
}
