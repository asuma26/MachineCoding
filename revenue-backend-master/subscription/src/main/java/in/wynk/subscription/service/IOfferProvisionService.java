package in.wynk.subscription.service;

import in.wynk.subscription.dto.OfferEligibilityCheckRequest;
import in.wynk.subscription.dto.OfferEligibilityCheckResponse;
import in.wynk.subscription.dto.OfferProvisionRequest;
import in.wynk.subscription.dto.response.OfferProvisionResponse;

import java.util.List;

public interface IOfferProvisionService {

    OfferProvisionResponse.OfferProvisionData provisionOffers(OfferProvisionRequest offerProvisionRequest);

    OfferEligibilityCheckResponse checkEligibility(OfferEligibilityCheckRequest offerEligibilityCheckRequest);

    List<OfferCheckEligibility> getEligibleOffers(OfferProvisionRequest offerProvisionRequest);

}
