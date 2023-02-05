package in.wynk.partner.service.impl;

import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.service.IOrderManager;
import in.wynk.partner.listing.dto.request.ActivePlansListingRequest;
import in.wynk.partner.listing.dto.request.EligiblePlansListingRequest;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;
import in.wynk.partner.listing.service.IListingService;
import in.wynk.partner.service.IPaymentPartnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PaymentPartnerServiceImpl implements IPaymentPartnerService {

    @Autowired
    private IOrderManager orderManager;

    @Autowired
    private IListingService listingService;

    @Autowired
    private IClientDetailsService<Client> clientDetailsService;

    @Override
    public Optional<Client> getPartnerDetails(String clientId) {
        return clientDetailsService.getClientDetails(clientId);
    }

    @Override
    public EligiblePlansListingResponse getAllPlans(String clientId, String service) {
        return listingService.getAllPlansToBeListed(EligiblePlansListingRequest.builder().clientId(clientId).service(service).build());
    }

    @Override
    public ActivePlansListingResponse getActivePlansForUser(String msisdn, String service) {
        return listingService.getAllActivePlansForUser(ActivePlansListingRequest.builder().service(service).uid(MsisdnUtils.getUidFromMsisdn(msisdn)).build());
    }

    @Override
    public OrderResponse placeOrder(String clientId, OrderPlacementRequest request) {
        return orderManager.placeOrder(clientId, request);
    }

    @Override
    public OrderResponse orderStatus(String orderId, String msisdn) {
        return orderManager.orderStatus(orderId, msisdn);
    }

}
