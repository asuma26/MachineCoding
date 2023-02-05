package in.wynk.subscription.service.impl;

import in.wynk.common.enums.AppId;
import in.wynk.spel.IRuleEvaluator;
import in.wynk.spel.builder.DefaultStandardExpressionContextBuilder;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.PlanContext;
import in.wynk.subscription.service.IPlanProvisionService;
import in.wynk.subscription.service.PlanCheckEligibility;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanProvisionServiceImpl implements IPlanProvisionService {

    private final IRuleEvaluator ruleEvaluator;
    private final SubscriptionCachingService subscriptionCachingService;

    public PlanProvisionServiceImpl(IRuleEvaluator ruleEvaluator, SubscriptionCachingService subscriptionCachingService) {
        this.ruleEvaluator = ruleEvaluator;
        this.subscriptionCachingService = subscriptionCachingService;
    }

    @Override
    public List<Plan> getEligiblePlans(int offerId, AppId appId, List<UserPlanDetails> userPlanDetailsList) {
        return subscriptionCachingService.getPlansForOffer(offerId).stream()
                .filter(plan -> ruleEvaluator.evaluate(subscriptionCachingService.getPlanRuleExpressions().get(plan.getId()),
                        () -> DefaultStandardExpressionContextBuilder.builder()
                                .rootObject(PlanCheckEligibility.builder()
                                        .planContext(PlanContext.builder()
                                                .planId(plan.getId())
                                                .appId(appId)
                                                .userPlanDetails(userPlanDetailsList)
                                                .build())
                                        .build())
                                .build(),
                        Boolean.class))
                .collect(Collectors.toList());
    }

}
