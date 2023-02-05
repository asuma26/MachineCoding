package in .wynk.utils.service.subscription.impl;

import com.github.annotation.analytic.core.service.AnalyticService;
import in .wynk.common.constant.BaseConstants;
import in .wynk.exception.WynkRuntimeException;
import in .wynk.subscription.core.dao.entity.Plan;
import in .wynk.subscription.core.dao.entity.Subscription;
import in .wynk.subscription.core.dao.entity.UserPlanDetails;
import in .wynk.subscription.core.service.IUserdataService;
import in .wynk.subscription.core.service.SubscriptionCachingService;
import in .wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.constant.WcfUtilsLoggingMarkers;
import in .wynk.utils.response.SubscriptionAndUserPlanResponse;
import in .wynk.utils.response.SubscriptionProductResponse;
import in .wynk.utils.response.UserPlanDetailResponse;
import in .wynk.utils.service.subscription.ISubscriptionUserPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static in .wynk.exception.constants.ExceptionConstants.ERROR_CODE;

@Service
@Slf4j
public class SubscriptionUserPlanService implements ISubscriptionUserPlanService {

    private final SubscriptionCachingService cachingService;

    private final IUserdataService userdataService;

    public SubscriptionUserPlanService(SubscriptionCachingService cachingService, IUserdataService userdataService) {
        this.cachingService = cachingService;
        this.userdataService = userdataService;
    }

    @Override
    public SubscriptionAndUserPlanResponse populateResponse(String uid, String service) {
        try {
            List < UserPlanDetails > userPlanDetailsList = userdataService.getAllUserPlanDetails(uid, service);
            SubscriptionAndUserPlanResponse subscriptionAndUserPlanResponse = populateSubscriptionPlanResponsePlanDetails(userPlanDetailsList);
            List < Subscription > subscriptions = userdataService.getAllSubscriptions(uid, service);
            return populateSubscriptionPlanResponseSubscriptionDetails(subscriptions, subscriptionAndUserPlanResponse);
        } catch (WynkRuntimeException ex) {
            log.error(ex.getMarker(),ex.getErrorTitle());
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private SubscriptionAndUserPlanResponse populateSubscriptionPlanResponsePlanDetails(List < UserPlanDetails > userPlanDetailsList) {
        try {
            if (CollectionUtils.isNotEmpty(userPlanDetailsList)) {
                List < UserPlanDetailResponse > userPlanDetailResponses = new ArrayList < > ();
                for (UserPlanDetails userPlanDetailIterator: userPlanDetailsList) {
                    UserPlanDetailResponse userPlanDetailResponse = UserPlanDetailResponse.builder().planId(userPlanDetailIterator.getPlanId())
                            .planType(userPlanDetailIterator.getPlanType())
                            .endDate(userPlanDetailIterator.getEndDate())
                            .startDate(userPlanDetailIterator.getStartDate()).build();
                    if (userPlanDetailResponse.getPlanType() != BaseConstants.FREE) {
                        Plan plan = cachingService.getPlan(userPlanDetailIterator.getPlanId());
                        if (plan != null) {
                            userPlanDetailResponse.setPlanPrice(plan.getPrice().getAmount());
                        } else {
                            log.error(WcfUtilsLoggingMarkers.PLANS_ERROR,"plan id not found in cache");
                            new WynkRuntimeException(WcfUtilsErrorType.WCF017);
                        }
                    }
                    userPlanDetailResponses.add(userPlanDetailResponse);
                }
                log.info("Active plans fetched for response");
                return SubscriptionAndUserPlanResponse.builder().activePlanDetail(userPlanDetailResponses).build();
            }
        } catch (WynkRuntimeException ex) {
            AnalyticService.update(ERROR_CODE, ex.getErrorCode());
            log.error(ex.getMarker(),ex.getErrorTitle());
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
        return SubscriptionAndUserPlanResponse.builder().build();
    }

    private SubscriptionAndUserPlanResponse populateSubscriptionPlanResponseSubscriptionDetails(List < Subscription > subscriptions, SubscriptionAndUserPlanResponse subscriptionAndUserPlanResponse) {
        try {
            if (CollectionUtils.isNotEmpty(subscriptions)) {
                subscriptionAndUserPlanResponse.setActiveSubscriptions(subscriptions.stream().map(subscription -> convertSubscriptionToProductResponse(subscription)).collect(Collectors.toList()));
                log.info("Active subscription products fetched for response");
                return subscriptionAndUserPlanResponse;
            }
        } catch (WynkRuntimeException ex) {
            AnalyticService.update(ERROR_CODE, ex.getErrorCode());
            log.error(ex.getMarker(),ex.getErrorTitle());
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
        return subscriptionAndUserPlanResponse;
    }

    private SubscriptionProductResponse convertSubscriptionToProductResponse(Subscription subscription) {
        return SubscriptionProductResponse.builder().productId(subscription.getProductId())
                .service(subscription.getService()).nextChargingDate(subscription.getNextChargingDate())
                .validTillDate(subscription.getValidTillDate()).subscriptionDate(subscription.getSubscriptionDate())
                .subscriptionEndDate(subscription.getSubscriptionEndDate()).build();
    }
}