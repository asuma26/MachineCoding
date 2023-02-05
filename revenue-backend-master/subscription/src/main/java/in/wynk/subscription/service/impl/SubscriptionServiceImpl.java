package in.wynk.subscription.service.impl;

import in.wynk.common.constant.BaseConstants;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.dto.SubscriptionProvisionRequest;
import in.wynk.subscription.dto.SubscriptionUnProvisionRequest;
import in.wynk.subscription.service.ISubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final IUserdataService userdataService;

    public SubscriptionServiceImpl(IUserdataService userdataService) {
        this.userdataService = userdataService;
    }

    @Override
    public List<Subscription> provision(SubscriptionProvisionRequest request) {
        List<Subscription> subscriptionToBeProvisioned = request.getOfferPlanProductsForProvision().getPlanProductMap()
                .entrySet().stream().flatMap(entry -> entry.getValue().stream().map(product -> buildSubscription(request, entry.getKey(), product))).collect(Collectors.toList());
        subscriptionToBeProvisioned.addAll(request.getOfferPlanProductsForProvision().getActivePaidPlanProductMap().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(product -> buildSubscriptionForPaidPlan(request, entry.getKey(), product))).collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(subscriptionToBeProvisioned))
            userdataService.addAllSubscriptions(request.getUid(), request.getService(), subscriptionToBeProvisioned);
        return mergeSubscriptions(subscriptionToBeProvisioned, request.getActiveSubscriptions());
    }

    @Override
    public void unProvision(SubscriptionUnProvisionRequest request) {
        List<UserPlanDetails> allUserPlanDetails = userdataService.getAllUserPlanDetails(request.getUid(), request.getService());
        Optional<UserPlanDetails> activePlanDetailOption = allUserPlanDetails.stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).filter(u -> u.getPlanId() == request.getPlanId()).findAny();
        activePlanDetailOption.ifPresent(planDetails -> {
            planDetails.setAutoRenew(false);
            planDetails.setUnsubscribeOn(new Date());
            userdataService.addUserPlanDetails(planDetails);
        });
    }

    private Subscription buildSubscription(SubscriptionProvisionRequest request, Plan plan, Product product) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put(BaseConstants.OFFER_ID, String.valueOf(plan.getLinkedOfferId()));
        metaData.put(BaseConstants.PLAN_ID, String.valueOf(plan.getId()));
        if (StringUtils.isNotEmpty(request.getMsisdn()))
            metaData.put(BaseConstants.MSISDN, request.getMsisdn());
        if (StringUtils.isNotEmpty(request.getReferenceId())) {
            metaData.put(BaseConstants.TRANSACTION_ID, request.getReferenceId());
        }

        Date validTill = new Date(System.currentTimeMillis() + plan.getPeriod().getTimeUnit().toMillis(plan.getPeriod().getValidity()));

        if (CollectionUtils.isNotEmpty(request.getActiveSubscriptions())) {
            Optional<Subscription> subsOptional = request.getActiveSubscriptions().stream().filter(s -> !s.isFree()).filter(subs -> product.getId() == subs.getProductId()).findAny();
            if (subsOptional.isPresent()) {
                Subscription oldActiveSubs = subsOptional.get();
                validTill = new Date(oldActiveSubs.getValidTillDate().getTime() + plan.getPeriod().getTimeUnit().toMillis(plan.getPeriod().getValidity()));
            }
        }

        return Subscription.builder()
                .service(request.getService())
                .uid(request.getUid())
                .packGroup(product.getPackGroup())
                .active(true)
                .autoRenewalOff(!request.isAutoRenew())
                .nextChargingDate(validTill)
                .partnerProductId(product.getId())
                .paymentMetaData(metaData)
                .paymentMethod(request.getPaymentMethod())
                .productId(product.getId())
                .renewalUnderProcess(false)
                .subscriptionDate(new Date())
                .subscriptionEndDate(validTill)
                .subscriptionInProgress(false)
                .validTillDate(validTill)
                .build();
    }

    private Subscription buildSubscriptionForPaidPlan(SubscriptionProvisionRequest request, Plan plan, Product product) {
        UserPlanDetails paidPlan = request.getActivePlanDetails().stream().filter(p->p.getPlanId()==plan.getId()).findAny().get();
        Map<String, String> metaData = new HashMap<>();
        metaData.put(BaseConstants.OFFER_ID, String.valueOf(plan.getLinkedOfferId()));
        metaData.put(BaseConstants.PLAN_ID, String.valueOf(plan.getId()));
        metaData.put(BaseConstants.MSISDN, request.getMsisdn());
        if (StringUtils.isNotEmpty(paidPlan.getReferenceId())) {
            metaData.put(BaseConstants.TRANSACTION_ID, paidPlan.getReferenceId());
        }
        Date validTill = paidPlan.getEndDate();

        if (CollectionUtils.isNotEmpty(request.getActiveSubscriptions())) {
            Optional<Subscription> subsOptional = request.getActiveSubscriptions().stream().filter(s -> !s.isFree()).filter(subs -> product.getId() == subs.getProductId()).findAny();
            if (subsOptional.isPresent()) {
                Subscription oldActiveSubs = subsOptional.get();
                validTill = new Date(oldActiveSubs.getValidTillDate().getTime() + plan.getPeriod().getTimeUnit().toMillis(plan.getPeriod().getValidity()));
            }
        }

        return Subscription.builder()
                .service(request.getService())
                .uid(request.getUid())
                .packGroup(product.getPackGroup())
                .active(true)
                .autoRenewalOff(!paidPlan.isAutoRenew())
                .nextChargingDate(validTill)
                .partnerProductId(product.getId())
                .paymentMetaData(metaData)
                .paymentMethod(paidPlan.getPaymentCode())
                .productId(product.getId())
                .renewalUnderProcess(false)
                .subscriptionDate(new Date())
                .subscriptionEndDate(validTill)
                .subscriptionInProgress(false)
                .validTillDate(validTill)
                .build();
    }

    private List<Subscription> mergeSubscriptions(List<Subscription> provisionedSubscriptions, List<Subscription> currentSubscriptions) {
        List<Subscription> mergedSubscriptions = new ArrayList<>();
        Set<String> packGroups = new HashSet<>();
        for (Subscription subscription : provisionedSubscriptions) {
            mergedSubscriptions.add(subscription);
            packGroups.add(subscription.getPackGroup());
        }
        for (Subscription subscription : currentSubscriptions) {
            if (!packGroups.contains(subscription.getPackGroup())) {
                mergedSubscriptions.add(subscription);
            }
        }
        return mergedSubscriptions;
    }

}
