package in.wynk.partner.data;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Abhishek
 * @created 19/09/20
 */
public class ChannelPartnerServiceImplTestData {

    public static EligiblePlansListingRequest eligiblePlansListingRequest() {
        return EligiblePlansListingRequest.builder().clientId(TestDataConstants.PAYTM).service(TestDataConstants.MUSIC).build();
    }

    public static ResponseEntity<PartnerEligiblePlansResponse> getResponseEntityPartnerEligiblePlansResponse() {
        return new ResponseEntity<>(getPartnerEligiblePlansResponse(), HttpStatus.OK);
    }

    public static PartnerEligiblePlansResponse getPartnerEligiblePlansResponse() {
        return PartnerEligiblePlansResponse.builder().eligiblePlanDetails(getEligiblePlanDetails()).build();
    }

    private static List<EligiblePlanDetails> getEligiblePlanDetails() {
        return Collections.singletonList(EligiblePlanDetails.builder().planTitle(TestDataConstants.TEST_PLAN).total(TestDataConstants.TEST_PLAN_AMOUNT).planId(TestDataConstants.TEST_PLAN_ID).build());
    }

    public static EligiblePlansListingResponse eligiblePlansListingResponse() {
        return EligiblePlansListingResponse.builder().clientEligiblePlans(getEligiblePlans().getClientEligiblePlans()).build();
    }

    private static EligiblePlansListingResponse getEligiblePlans() {
        return EligiblePlansListingResponse.builder()
                .clientEligiblePlans(Collections.singletonList(ClientEligiblePlan.builder()
                        .planTitle(TestDataConstants.TEST_PLAN)
                        .planPrice(TestDataConstants.TEST_PLAN_AMOUNT)
                        .planId(TestDataConstants.TEST_PLAN_ID)
                        .build())).build();
    }

    public static ActivePlansListingRequest activePlansListingRequest() {
        return ActivePlansListingRequest.builder().uid(TestDataConstants.UID).service(TestDataConstants.MUSIC).build();
    }

    public static ResponseEntity<UserActivePlansResponse> getResponseEntityUserActivePlansResponse() {
        return new ResponseEntity<>(userActivePlansResponse(), HttpStatus.OK);
    }

    private static UserActivePlansResponse userActivePlansResponse() {
        return UserActivePlansResponse.builder().activePlanDetails(activePlanDetails()).build();
    }

    private static List<ActivePlanDetails> activePlanDetails() {
        return Collections.singletonList(ActivePlanDetails.builder().paymentChannel(TestDataConstants.TEST_ACTIVE_PLAN_PAYMENT_CHANNEL).validTillDate(TestDataConstants.TEST_ACTIVE_PLAN_VALID_TILL_DATE).planId(TestDataConstants.TEST_ACTIVE_PLAN_ID).build());
    }

    public static ActivePlansListingResponse activePlansListingResponse() {
        return ActivePlansListingResponse.builder().activePlans(activePlans().getActivePlans()).build();
    }

    private static ActivePlansListingResponse activePlans() {
        return ActivePlansListingResponse.builder().activePlans(Collections.singletonList(UserActivePlan.builder().paymentChannel(TestDataConstants.TEST_ACTIVE_PLAN_PAYMENT_CHANNEL).planId(TestDataConstants.TEST_ACTIVE_PLAN_ID).validTillDate(TestDataConstants.TEST_ACTIVE_PLAN_VALID_TILL_DATE.getTime()).build())).build();
    }
}
