package in.wynk.partner.service;

import in.wynk.auth.dao.entity.Client;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;

import java.util.Optional;

public interface IPaymentPartnerService {

    Optional<Client> getPartnerDetails(String clientId);

    EligiblePlansListingResponse getAllPlans(String clientId, String service);

    ActivePlansListingResponse getActivePlansForUser(String msisdn, String service);

    OrderResponse placeOrder(String clientId, OrderPlacementRequest request);

    OrderResponse orderStatus(String orderId, String msisdn);

}
