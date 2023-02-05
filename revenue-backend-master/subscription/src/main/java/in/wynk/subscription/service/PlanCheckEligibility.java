package in.wynk.subscription.service;

import in.wynk.common.enums.AppId;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.dto.PlanContext;
import in.wynk.subscription.enums.OfferEligibilityStatus;
import in.wynk.subscription.enums.OfferEligibilityStatusReason;
import lombok.Builder;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public class PlanCheckEligibility {

    private final PlanContext planContext;

    public boolean init() {
        Integer planId = planContext.getPlanId();
        List<UserPlanDetails> userPlanDetailsList = planContext.getUserPlanDetails();
        if (CollectionUtils.isNotEmpty(userPlanDetailsList)) {
            Optional<UserPlanDetails> userPlanDetailsOptional = userPlanDetailsList.stream().filter(userPlanDetails -> userPlanDetails.getPlanId() == planId).findAny();
            if (userPlanDetailsOptional.isPresent() && userPlanDetailsOptional.get().isFreeActiveOrPaidActive()) {
                return false;
            }
        }
        return true;
    }

    public boolean limitUser(int limit) {
        int planId = planContext.getPlanId();
        List<UserPlanDetails> userPlanDetails = planContext.getUserPlanDetails();
        if (CollectionUtils.isNotEmpty(userPlanDetails)) {
            List<UserPlanDetails> eligiblePlans = userPlanDetails.stream()
                    .filter(details -> details.getPlanId() == planId && details.getPlanCount() >= limit)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(eligiblePlans)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasNotAvailedPlans(Integer... planIds) {
        Set<Integer> availedPlanIds = planContext.getUserPlanDetails().stream().map(UserPlanDetails::getPlanId).collect(Collectors.toSet());
        Optional<Integer> availedPlanId = Arrays.stream(planIds).filter(availedPlanIds::contains).findAny();
        if (availedPlanId.isPresent()) {
            return false;
        }
        return true;
    }

    public boolean hasActivePlans(Integer... planIds) {
        Set<Integer> activePlanIds = planContext.getUserPlanDetails().stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).map(UserPlanDetails::getPlanId).collect(Collectors.toSet());
        Optional<Integer> activePlanIdOption = Arrays.stream(planIds).filter(activePlanIds::contains).findAny();
        return activePlanIdOption.isPresent();
    }

    public boolean hasAppId(String... appIds) {
        final boolean eligible;
        AppId currentAppId = planContext.getAppId();
        if (currentAppId == null || currentAppId == AppId.UNKNOWN) {
            eligible = false;
        } else {
            Set<AppId> availableAppIds = Arrays.stream(appIds).map(AppId::fromString).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(availableAppIds) && availableAppIds.contains(currentAppId)) {
                eligible = true;
            } else {
                eligible = false;
            }
        }
        return eligible;
    }

}
