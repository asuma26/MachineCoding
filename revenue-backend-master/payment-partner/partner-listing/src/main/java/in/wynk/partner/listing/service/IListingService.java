package in.wynk.partner.listing.service;

import in.wynk.partner.listing.dto.request.ActivePlansListingRequest;
import in.wynk.partner.listing.dto.request.EligiblePlansListingRequest;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;

public interface IListingService {

    EligiblePlansListingResponse getAllPlansToBeListed(EligiblePlansListingRequest eligiblePlansListingRequest);

    ActivePlansListingResponse getAllActivePlansForUser(ActivePlansListingRequest activePlansListingRequest);
}
