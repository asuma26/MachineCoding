package in.wynk.subscription.test.data;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static in.wynk.subscription.test.data.OfferCheckEligibilityTestConstants.*;

/**
 * @author Abhishek
 * @created 28/09/20
 */
public class OfferCheckEligibilityTestData {

    public static List<UserPlanDetails> successUserPlanDetails(int days, int planCount){
        return Collections.singletonList(UserPlanDetails.builder().planId(PLAN_ID).planCount(planCount).service(WynkService.AIRTEL_TV.getValue()).endDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days-1))).build());
    }

    public static List<UserPlanDetails> failureUserPlanDetails(int days, int planCount){
        return Collections.singletonList(UserPlanDetails.builder().planId(PLAN_ID).planCount(planCount).service(WynkService.AIRTEL_TV.getValue()).endDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days+1))).build());
    }

    public static Offer offer(){
        return Offer.builder().id(OFFER_ID).hierarchy(HIERARCHY).provisionType(ProvisionType.FREE).plans(getPlanIds()).build();
    }

    public static List<Integer> getPlanIds(){
        return Collections.singletonList(PLAN_ID);
    }
}
