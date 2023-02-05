package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.utils.BeanLocatorFactory;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;

import java.util.concurrent.TimeUnit;

import static in.wynk.common.constant.BaseConstants.PLAN_ID;

@Getter
@AllArgsConstructor
@AnalysedEntity
public class SubscriptionStatus {

    private final String service;
    private final boolean active;
    @Analysed
    private final int productId;
    @Analysed
    private final String packGroup;
    @Analysed
    private long validity;
    @Analysed
    private int planId;

    @Deprecated
    public SubscriptionStatus(Subscription subscription) {
        this.active = subscription.isActive();
        this.packGroup = subscription.getPackGroup();
        this.service = subscription.getService();
        this.productId = subscription.getProductId();
        this.validity = subscription.getValidTillDate().getTime();
        if (!subscription.isSubscriptionActive()) {
            Plan plan = BeanLocatorFactory.getBean(SubscriptionCachingService.class).getPlan(subscription.getPaymentMetaData().get(PLAN_ID));
            if (plan != null) {
                int grace = plan.getPeriod().getGrace();
                TimeUnit timeUnit = plan.getPeriod().getTimeUnit();
                if (subscription.isSubscriptionInGracePeriod(timeUnit, grace)) {
                    this.validity = subscription.getValidTillWithGracePeriod(timeUnit, grace);
                }
            }
        }
    }

    public SubscriptionStatus(Pair<UserPlanDetails, Product> planProductDetails) {
        UserPlanDetails activePlanDetails = planProductDetails.getFirst();
        Product activeProduct = planProductDetails.getSecond();
        validity = activePlanDetails.getEndDate().getTime();
        active = activePlanDetails.isFreeActiveOrPaidActive();
        service = activePlanDetails.getService();
        packGroup = activeProduct.getPackGroup();
        productId = activeProduct.getId();
        planId = activePlanDetails.getPlanId();
        Plan activePlan = BeanLocatorFactory.getBean(SubscriptionCachingService.class).getPlan(activePlanDetails.getPlanId());
        if (activePlanDetails.isSubscriptionInGracePeriod(activePlan.getPeriod().getTimeUnit(), activePlan.getPeriod().getGrace())) {
            validity = activePlanDetails.getValidTillWithGracePeriod(activePlan.getPeriod().getTimeUnit(), activePlan.getPeriod().getGrace());
        }
    }

}