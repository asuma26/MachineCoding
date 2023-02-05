package in.wynk.partner.listing.service.impl;

import in.wynk.client.aspect.advice.ClientAware;
import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.partner.common.dto.ActivePlanDetails;
import in.wynk.partner.common.dto.EligiblePlanDetails;
import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.partner.listing.dto.ClientEligiblePlan;
import in.wynk.partner.listing.dto.UserActivePlan;
import in.wynk.partner.listing.dto.request.ActivePlansListingRequest;
import in.wynk.partner.listing.dto.request.EligiblePlansListingRequest;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;
import in.wynk.partner.listing.service.IListingService;
import in.wynk.partner.listing.service.PartnerApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanListingServiceImpl implements IListingService {

    @Autowired
    private PartnerApiService partnerApiService;

    @Override
    @ClientAware(clientId = "#eligiblePlansListingRequest.clientId")
    public EligiblePlansListingResponse getAllPlansToBeListed(EligiblePlansListingRequest eligiblePlansListingRequest) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        PartnerEligiblePlansResponse partnerEligiblePlansResponse = partnerApiService.getAllPlansForPartner(clientDetails.getAlias(), eligiblePlansListingRequest.getService());
        List<ClientEligiblePlan> clientEligiblePlans = new ArrayList<>();
        if (partnerEligiblePlansResponse != null) {
            for (EligiblePlanDetails response : partnerEligiblePlansResponse.getEligiblePlanDetails()) {
                clientEligiblePlans.add(ClientEligiblePlan.builder().planId(response.getPlanId()).planPrice(response.getTotal()).planTitle(response.getPlanTitle()).build());
            }
        }
        return EligiblePlansListingResponse.builder().clientEligiblePlans(clientEligiblePlans).build();
    }

    @Override
    public ActivePlansListingResponse getAllActivePlansForUser(ActivePlansListingRequest activePlansListingRequest) {
        UserActivePlansResponse userActivePlansResponse = partnerApiService.getAllActivePlans(activePlansListingRequest.getUid(), activePlansListingRequest.getService());
        List<UserActivePlan> activePlans = new ArrayList<>();
        if (userActivePlansResponse != null) {
            for (ActivePlanDetails activePlanDetails : userActivePlansResponse.getActivePlanDetails()) {
                activePlans.add(UserActivePlan.builder().planId(activePlanDetails.getPlanId()).paymentChannel(activePlanDetails.getPaymentChannel()).validTillDate(activePlanDetails.getValidTillDate().getTime()).build());
            }
        }
        return ActivePlansListingResponse.builder().activePlans(activePlans).build();
    }

}
