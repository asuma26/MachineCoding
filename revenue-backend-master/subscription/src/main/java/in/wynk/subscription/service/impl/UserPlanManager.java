package in.wynk.subscription.service.impl;

import in.wynk.common.constant.BaseConstants;
import in.wynk.common.dto.SessionDTO;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.enums.TransactionStatus;
import in.wynk.common.enums.WynkService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.dto.CancellationOrder;
import in.wynk.order.common.message.OrderPlacementMessage;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.session.context.SessionContextHolder;
import in.wynk.spel.IRuleEvaluator;
import in.wynk.spel.builder.DefaultStandardExpressionContextBuilder;
import in.wynk.subscription.common.dto.*;
import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.common.enums.ProvisionState;
import in.wynk.subscription.core.constants.SubscriptionConstant;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.events.SubscriptionEvent;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.MultiPlanProvisionRequest;
import in.wynk.subscription.dto.OfferPlanProductsForProvision;
import in.wynk.subscription.dto.SubscriptionProvisionRequest;
import in.wynk.subscription.dto.request.EligibleProductsComputationRequest;
import in.wynk.subscription.dto.response.EligibleProductsComputationResponse;
import in.wynk.subscription.dto.response.MultiPlanProvisioningResponse;
import in.wynk.subscription.event.NotificationEvent;
import in.wynk.subscription.service.IUserPlanManager;
import in.wynk.subscription.service.IUserProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static in.wynk.common.enums.PaymentEvent.*;

@Slf4j
@Service
public class UserPlanManager implements IUserPlanManager {

    private final IRuleEvaluator ruleEvaluator;
    private final IUserdataService userdataService;
    private final IUserProductService productService;
    private final ISqsManagerService sqsManagerService;
    private final ApplicationEventPublisher eventPublisher;
    private final SubscriptionCachingService cachingService;

    public UserPlanManager(IRuleEvaluator ruleEvaluator, IUserdataService userdataService, IUserProductService productService, ISqsManagerService sqsManagerService, SubscriptionCachingService cachingService, ApplicationEventPublisher eventPublisher) {
        this.ruleEvaluator = ruleEvaluator;
        this.productService = productService;
        this.cachingService = cachingService;
        this.eventPublisher = eventPublisher;
        this.userdataService = userdataService;
        this.sqsManagerService = sqsManagerService;
    }

    @Override
    public ProvisioningResponse subscribe(PlanProvisioningRequest request) {
        final ProvisioningResponse response;
        if (request instanceof MultiPlanProvisionRequest) {
            response = subscribe(((MultiPlanProvisionRequest) request));
        } else {
            response = subscribe(((SinglePlanProvisionRequest) request));
        }
        return response;
    }

    private ProvisioningResponse subscribe(MultiPlanProvisionRequest request) {
        Collection<UserPlanDetails> combinedPlanDetails = new HashSet<>();
        Set<Plan> plansToBeSubscribed = request.getPlanIds().stream().map(cachingService::getPlan).collect(Collectors.toSet());
        List<UserPlanDetails> activePlansDetails = request.getUserPlanDetails().stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).collect(Collectors.toList());
        Map<Integer, Integer> activePlansCountMap = request.getUserPlanDetails().stream().collect(Collectors.toMap(UserPlanDetails::getPlanId, UserPlanDetails::getPlanCount));
        Set<Plan> activePlans = activePlansDetails.stream().map(UserPlanDetails::getPlanId).filter(cachingService::containsPlan).map(cachingService::getPlan).collect(Collectors.toSet());
        EligibleProductsComputationResponse productsComputationResponse = productService.compute(EligibleProductsComputationRequest.builder().activePlans(activePlans).plansToBeSubscribed(plansToBeSubscribed).build());
        List<UserPlanDetails> eligiblePlanDetails = productsComputationResponse.getEligiblePlanProductMap().keySet().stream().filter(cachingService::containsPlan).map(cachingService::getPlan).map(planId -> buildUserPlanDetails(request, planId, activePlansCountMap)).collect(Collectors.toList());
        List<UserPlanDetails> deactivatedPlans = unsubscribeInEligiblePlan(activePlansDetails, productsComputationResponse);
        if (CollectionUtils.isNotEmpty(eligiblePlanDetails)) {
            if (CollectionUtils.isNotEmpty(deactivatedPlans)) {
                eligiblePlanDetails.addAll(deactivatedPlans);
            }
            userdataService.addAllUserPlanDetails(request.getUid(), request.getService().getValue(), eligiblePlanDetails);
            publishNotificationToUser(request.getMsisdn(), plansToBeSubscribed, eligiblePlanDetails);
        } else if (CollectionUtils.isNotEmpty(deactivatedPlans)) {
            userdataService.addAllUserPlanDetails(request.getUid(), request.getService().getValue(), deactivatedPlans);
        }
        combinedPlanDetails.addAll(eligiblePlanDetails);
        combinedPlanDetails.addAll(activePlansDetails);
        Map<UserPlanDetails, Set<Product>> provisionedPlanProductsDetails = combinedPlanDetails.stream().filter(planDetails -> productsComputationResponse.getCombinedPlanProductMap().containsKey(planDetails.getPlanId())).collect(Collectors.toMap(Function.identity(), planDetails -> productsComputationResponse.getCombinedPlanProductMap().get(planDetails.getPlanId())));
        return MultiPlanProvisioningResponse.builder().provisionedPlanProductsDetails(provisionedPlanProductsDetails).build();
    }

    private List<UserPlanDetails> unsubscribeInEligiblePlan(List<UserPlanDetails> activePlanDetails, EligibleProductsComputationResponse response) {
        return unsubscribeInEligiblePlanInternal(activePlanDetails, Optional.empty(), response);
    }

    private List<UserPlanDetails> unsubscribeInEligiblePlanInternal(List<UserPlanDetails> activePlanDetails, Optional<UserPlanDetails> planToBeProvisionedOptional, EligibleProductsComputationResponse response) {
        List<Integer> combinedPlanIds = new ArrayList<>(response.getCombinedPlanProductMap().keySet());
        if (CollectionUtils.isNotEmpty(activePlanDetails) & CollectionUtils.isNotEmpty(combinedPlanIds)) {
            List<UserPlanDetails> deactivatedPlans = activePlanDetails.stream().filter(planDetails -> !combinedPlanIds.contains(planDetails.getPlanId())).collect(Collectors.toList());
            List<UserPlanDetails> freeDeactivatedPlans = deactivatedPlans.stream().filter(UserPlanDetails::isFree).collect(Collectors.toList());
            List<UserPlanDetails> renewableDeactivatedPlans = deactivatedPlans.stream().filter(UserPlanDetails::isPaid).filter(UserPlanDetails::isAutoRenew).collect(Collectors.toList());
            freeDeactivatedPlans.forEach(u -> u.setEndDate(new Date()));
            response.getEligiblePlanProductMap().keySet().stream().filter(cachingService::containsPlan).map(cachingService::getPlan).findAny().ifPresent(plan -> renewableDeactivatedPlans.forEach(u -> planToBeProvisionedOptional.map(p -> p.getEndDate().getTime() - u.getEndDate().getTime()).filter(t -> t > 0).ifPresent(offset -> publishSubscriptionNotificationInternal(u, offset, DEFERRED))));
            return freeDeactivatedPlans;
        }
        return Collections.emptyList();
    }

    private PlanProvisioningResponse subscribe(SinglePlanProvisionRequest request) {
        Plan plan = cachingService.getPlan(request.getPlanId());
        List<UserPlanDetails> activePlanDetailsList = new ArrayList<>(userdataService.getAllUserPlanDetails(request.getUid(), plan.getService()));
        Map<Integer, Integer> userPlanCountMap = activePlanDetailsList.stream().collect(Collectors.toMap(UserPlanDetails::getPlanId, UserPlanDetails::getPlanCount));
        activePlanDetailsList = activePlanDetailsList.stream().filter(UserPlanDetails::isActive).collect(Collectors.toList());

        if (request.getStatus() == TransactionStatus.MIGRATED) {
            Optional<UserPlanDetails> optional = activePlanDetailsList.stream().filter(userPlanDetails -> userPlanDetails.getPlanId() == request.getPlanId()).findAny();
            if (optional.isPresent()) {
                UserPlanDetails userPlanDetails = optional.get();
                userPlanDetails.setReferenceId(request.getReferenceId());
                userdataService.addUserPlanDetails(userPlanDetails);
                return PlanProvisioningResponse.builder().planId(userPlanDetails.getPlanId()).state(ProvisionState.MIGRATED).autoRenew(userPlanDetails.isAutoRenew()).endDate(userPlanDetails.getEndDate().getTime()).build();
            }
            return PlanProvisioningResponse.builder().planId(request.getPlanId()).autoRenew(isAutoRenew(request)).state(ProvisionState.UNKNOWN).build();
        }

        Optional<UserPlanDetails> planToBeProvisionedOptional = Optional.empty();
        //List<Subscription> activeSubscriptions = userdataService.getAllSubscriptions(request.getUid(), plan.getService()).stream().filter(subs -> subs.isFree() || (!subs.isFree() && subs.isSubscriptionInGracePeriod(cachingService.getPlan(Integer.parseInt(subs.getPaymentMetaData().get(PLAN_ID))).getPeriod().getTimeUnit(), cachingService.getPlan(Integer.parseInt(subs.getPaymentMetaData().get(PLAN_ID))).getPeriod().getGrace()))).collect(Collectors.toList());
        Map<Integer, UserPlanDetails> activeUserPlanDetailsMap = activePlanDetailsList.stream().collect(Collectors.toMap(UserPlanDetails::getPlanId, Function.identity()));
        Set<Plan> activePlans = activePlanDetailsList.stream().map(UserPlanDetails::getPlanId).filter(cachingService::containsPlan).map(cachingService::getPlan).collect(Collectors.toSet());
        EligibleProductsComputationResponse productsComputationResponse = productService.compute(EligibleProductsComputationRequest.builder().activePlans(activePlans).plansToBeSubscribed(Collections.singleton(plan)).build());

        long planStartDate = -1;

        log.info("Subscribing plan: {} to user: {} for service: {}", request.getPlanId(), request.getUid(), plan.getService());
        if (CollectionUtils.isNotEmpty(activePlanDetailsList)) {
            Optional<UserPlanDetails> samePlanOption = activePlanDetailsList.stream().filter(planDetails -> planDetails.getOfferId() == plan.getLinkedOfferId() && cachingService.getPlan(planDetails.getPlanId()).compareTo(cachingService.getPlan(plan.getId())) == 0).findAny();
            if (samePlanOption.isPresent()) {
                planStartDate = samePlanOption.get().getEndDate().getTime();
                planToBeProvisionedOptional = Optional.of(buildUserPlanDetails(request, plan, userPlanCountMap, samePlanOption));
            }

            Optional<UserPlanDetails> upGradePlanOption = activePlanDetailsList.stream().filter(planDetails -> planDetails.getOfferId() == plan.getLinkedOfferId() && cachingService.getPlan(planDetails.getPlanId()).compareTo(cachingService.getPlan(plan.getId())) < 0).findAny();
            if (upGradePlanOption.isPresent()) {
                planStartDate = upGradePlanOption.get().getEndDate().getTime();
                //upGradePlanOption.ifPresent(planDetails -> unsubscribe(PlanUnProvisioningRequest.builder().uid(request.getUid()).planId(planDetails.getPlanId()).paymentPartner(request.getPaymentPartner()).referenceId(request.getReferenceId()).build()));
                planToBeProvisionedOptional = Optional.of(buildUserPlanDetails(request, plan, userPlanCountMap, upGradePlanOption));
            }

            Optional<UserPlanDetails> downGradePlanOption = activePlanDetailsList.stream().filter(planDetails -> planDetails.getOfferId() == plan.getLinkedOfferId() && cachingService.getPlan(planDetails.getPlanId()).compareTo(cachingService.getPlan(plan.getId())) > 0).findAny();
            if (downGradePlanOption.isPresent()) {
                planStartDate = downGradePlanOption.get().getEndDate().getTime();
                planToBeProvisionedOptional = Optional.of(buildUserPlanDetails(request, plan, userPlanCountMap, downGradePlanOption));
            }

            Optional<UserPlanDetails> differentOfferOption = activePlanDetailsList.stream().filter(planDetails -> planDetails.getOfferId() == plan.getLinkedOfferId()).findAny();
            if (!differentOfferOption.isPresent()) {
                Optional<Plan> productToBeProvisionedOptional = Optional.ofNullable(productsComputationResponse.getEligiblePlanProductMap().containsKey(plan.getId()) ? plan : null);
                if (!productToBeProvisionedOptional.isPresent()) {
                    log.info("Deferring plan: {} to user: {} for service: {}", request.getPlanId(), request.getUid(), plan.getService());
                    Collection<String> packGroupToBeProvisioned = cachingService.getOffer(plan.getLinkedOfferId()).getProducts().values();
                    Map<String, Pair<Integer, Product>> packGroupToPlanProducts = productsComputationResponse.getCombinedPlanProductMap().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(p -> Pair.of(entry.getKey(), p))).collect(Collectors.toMap(pair -> pair.getSecond().getPackGroup(), Function.identity()));
                    List<UserPlanDetails> deferCausingSubscriptions = packGroupToPlanProducts.entrySet().stream().filter(entry -> packGroupToBeProvisioned.contains(entry.getKey())).map(Map.Entry::getValue).map(Pair::getFirst).filter(activeUserPlanDetailsMap::containsKey).map(activeUserPlanDetailsMap::get).collect(Collectors.toList());
                    List<UserPlanDetails> deferCausingRenewalSubscriptions = deferCausingSubscriptions.stream().filter(UserPlanDetails::isAutoRenew).collect(Collectors.toList());
                    List<UserPlanDetails> deferCausingNonRenewalSubscriptions = deferCausingSubscriptions.stream().filter(planDetails -> !planDetails.isAutoRenew()).collect(Collectors.toList());
                    long deferredUntil;
                    if (CollectionUtils.isNotEmpty(deferCausingRenewalSubscriptions)) {
                        deferredUntil = deferCausingRenewalSubscriptions.stream().map(this::getValidTillDatePlusGraceForRenewal).reduce(Long::max).orElse(System.currentTimeMillis());
                    } else {
                        deferredUntil = deferCausingNonRenewalSubscriptions.stream().map(planDetails -> planDetails.getEndDate().getTime()).reduce(Long::max).orElse(System.currentTimeMillis());
                    }
                    long activeUntil = deferredUntil + plan.getPeriod().getTimeUnit().toMillis(plan.getPeriod().getValidity());
                    return PlanProvisioningResponse.builder().planId(plan.getId()).state(ProvisionState.DEFERRED).autoRenew(plan.getType() == PlanType.SUBSCRIPTION).startDate(deferredUntil).endDate(activeUntil).build();
                } else {
                    planToBeProvisionedOptional = Optional.of(buildUserPlanDetails(request, plan, userPlanCountMap, Optional.empty()));
                }
            }

        } else {
            planToBeProvisionedOptional = Optional.of(buildUserPlanDetails(request, plan, userPlanCountMap, Optional.empty()));
        }

        if (planToBeProvisionedOptional.isPresent()) {
            UserPlanDetails planDetailsToBeProvisioned = planToBeProvisionedOptional.get();
            List<UserPlanDetails> deactivatedPlans = unsubscribeInEligiblePlanInternal(activePlanDetailsList, planToBeProvisionedOptional, productsComputationResponse);
            if (CollectionUtils.isNotEmpty(deactivatedPlans)) {
                deactivatedPlans.add(planDetailsToBeProvisioned);
                userdataService.addAllUserPlanDetails(request.getUid(), plan.getService(), deactivatedPlans);
            } else {
                userdataService.addUserPlanDetails(planDetailsToBeProvisioned);
            }
            publishSubscriptionNotification(planDetailsToBeProvisioned, SUBSCRIBE);
            publishNotificationToUser(request.getMsisdn(), Collections.singleton(plan), Collections.singletonList(planDetailsToBeProvisioned));
            return PlanProvisioningResponse.builder()
                    .startDate(planStartDate > 0 ? planStartDate : planDetailsToBeProvisioned.getStartDate().getTime())
                    .endDate(planDetailsToBeProvisioned.getEndDate().getTime())
                    .autoRenew(planDetailsToBeProvisioned.isAutoRenew())
                    .planId(planDetailsToBeProvisioned.getPlanId())
                    .state(ProvisionState.SUBSCRIBED)
                    .build();
        } else {
            return PlanProvisioningResponse.builder()
                    .planId(request.getPlanId())
                    .state(ProvisionState.UNKNOWN)
                    .build();
        }
    }

    private long getValidTillDatePlusGraceForRenewal(UserPlanDetails details) {
        if (cachingService.containsPlan(details.getPlanId())) {
            Period period = cachingService.getPlan(details.getPlanId()).getPeriod();
            return details.getEndDate().getTime() + period.getTimeUnit().toMillis(period.getGrace());
        }
        return 0L;
    }

    @Override
    public PlanProvisioningResponse unsubscribe(PlanUnProvisioningRequest request) {
        log.info("Un-subscribing plan: {} for uid: {}", request.getPlanId(), request.getUid());
        Plan activePlan = cachingService.getPlan(request.getPlanId());
        List<UserPlanDetails> activePlansDetails = userdataService.getAllUserPlanDetails(request.getUid(), activePlan.getService());
        Optional<UserPlanDetails> planDetailsOption = activePlansDetails.stream().filter(planDetails -> planDetails.isActive() && planDetails.getUnsubscribeOn() == null)
                .filter(planDetails -> planDetails.getPlanId() == request.getPlanId())
                .findFirst();

        if (planDetailsOption.isPresent()) {
            UserPlanDetails planDetails = planDetailsOption.get();
            if (request.getPaymentPartner().equalsIgnoreCase(BaseConstants.WYNK) && !request.getPaymentPartner().equalsIgnoreCase(planDetails.getPaymentChannel())) {
                String msisdn = SessionContextHolder.<SessionDTO>getBody().get(BaseConstants.MSISDN);
                sqsManagerService.publishSQSMessage(OrderPlacementMessage.builder()
                        .partnerId(BaseConstants.WYNK.toLowerCase())
                        .msisdn(msisdn)
                        .partnerOrder(CancellationOrder.builder()
                                .id(planDetails.getReferenceId())
                                .planId(planDetails.getPlanId())
                                .reason("cancelled by user")
                                .build())
                        .build());
            }
            return unsubscribeNow(planDetails);
        } else {
            throw new WynkRuntimeException("plan is not found to be unsubscribed");
        }
    }

    private PlanProvisioningResponse unsubscribeNow(UserPlanDetails planDetails) {
        planDetails.setUnsubscribeOn(Calendar.getInstance().getTime());
        planDetails.setAutoRenew(false);
        userdataService.addUserPlanDetails(planDetails);
        publishSubscriptionNotification(planDetails, UNSUBSCRIBE);
        return PlanProvisioningResponse.builder().planId(planDetails.getPlanId()).startDate(planDetails.getStartDate().getTime()).endDate(planDetails.getEndDate().getTime()).state(ProvisionState.UNSUBSCRIBED).autoRenew(false).build();
    }

    private UserPlanDetails buildUserPlanDetails(PlanProvisioningRequest request, Plan planToBeProvisioned, Map<Integer, Integer> activePlansCount) {
        int subscribePlanCount = 1;
        if (MapUtils.isNotEmpty(activePlansCount) && activePlansCount.containsKey(planToBeProvisioned.getId())) {
            subscribePlanCount = activePlansCount.get(planToBeProvisioned.getId()) + 1;
        }
        Date validTill = new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(planToBeProvisioned.getPeriod().getValidity(), TimeUnit.DAYS));
        return UserPlanDetails.builder()
                .uid(request.getUid())
                .service(planToBeProvisioned.getService())
                .endDate(validTill)
                .planId(planToBeProvisioned.getId())
                .offerId(planToBeProvisioned.getLinkedOfferId())
                .planType(getPlanType(request, planToBeProvisioned))
                .paymentCode(request.getPaymentCode())
                .referenceId(request.getReferenceId())
                .paymentChannel(request.getPaymentPartner())
                .planCount(subscribePlanCount)
                .startDate(Calendar.getInstance().getTime())
                .autoRenew(isAutoRenew(request))
                .build();
    }

    private UserPlanDetails buildUserPlanDetails(PlanProvisioningRequest request, Plan planToBeProvisioned,  Map<Integer, Integer> allUserPlansCountMap, Optional<UserPlanDetails> activePlanOption) {
        final long validTill;
        final int subscribePlanCount = allUserPlansCountMap.getOrDefault(planToBeProvisioned.getId(), 0) + 1;
        validTill = activePlanOption.map(planDetails -> planDetails.getEndDate().getTime() + planToBeProvisioned.getPeriod().getTimeUnit().toMillis(planToBeProvisioned.getPeriod().getValidity())).orElseGet(() -> System.currentTimeMillis() + planToBeProvisioned.getPeriod().getTimeUnit().toMillis(planToBeProvisioned.getPeriod().getValidity()));
        return UserPlanDetails.builder()
                .planType(getPlanType(request, planToBeProvisioned))
                .autoRenew(isAutoRenew(request))
                .offerId(planToBeProvisioned.getLinkedOfferId())
                .paymentChannel(request.getPaymentPartner())
                .startDate(Calendar.getInstance().getTime())
                .service(planToBeProvisioned.getService())
                .paymentCode(request.getPaymentCode())
                .referenceId(request.getReferenceId())
                .planId(planToBeProvisioned.getId())
                .planCount(subscribePlanCount)
                .uid(request.getUid())
                .endDate(new Date(validTill))
                .build();
    }

    private boolean isAutoRenew(PlanProvisioningRequest request) {
        return request.getEventType() == PaymentEvent.SUBSCRIBE || request.getEventType() == PaymentEvent.RENEW || request.getEventType() == PaymentEvent.TRIAL_SUBSCRIPTION;
    }

    private String getPlanType(PlanProvisioningRequest request, Plan planToBeProvisioned) {
        return isAutoRenew(request) ? (request.getEventType() == PaymentEvent.TRIAL_SUBSCRIPTION ? PlanType.FREE_TRIAL.getValue(): PlanType.SUBSCRIPTION.getValue()) : (planToBeProvisioned.getType() == PlanType.SUBSCRIPTION ? PlanType.ONE_TIME_SUBSCRIPTION.getValue() : planToBeProvisioned.getType().getValue());
    }

    /*
     * TODO: can we supply service via planProvisionRequest supplied via offerProcessingService, Order Service, Payment Service
     */
    private SubscriptionProvisionRequest buildSubscriptionProvisionRequest(String service, List<Subscription> activeSubscriptions, OfferPlanProductsForProvision offerPlanProductsForProvision, PlanProvisioningRequest request, List<UserPlanDetails> activePlanDetails) {
        return SubscriptionProvisionRequest.builder()
                .uid(request.getUid())
                .msisdn(request.getMsisdn())
                .service(service)
                .referenceId(request.getReferenceId())
                .paymentMethod(request.getPaymentCode())
                .activeSubscriptions(activeSubscriptions)
                .activePlanDetails(activePlanDetails)
                .offerPlanProductsForProvision(offerPlanProductsForProvision)
                .autoRenew(isAutoRenew(request))
                .build();
    }

    private void publishSubscriptionNotification(UserPlanDetails details, PaymentEvent event) {
        publishSubscriptionNotificationInternal(details, 0, event);
    }

    private void publishSubscriptionNotificationInternal(UserPlanDetails details, long deferredUntil, PaymentEvent event) {
        eventPublisher.publishEvent(SubscriptionEvent.builder()
                .uid(details.getUid())
                .active(details.isActive())
                .planId(details.getPlanId())
                .offerId(details.getOfferId())
                .event(event)
                .deferredUntil(deferredUntil)
                .status(TransactionStatus.SUCCESS)
                .autoRenewal(details.isAutoRenew())
                .referenceId(details.getReferenceId())
                .validTillDate(details.getEndDate().getTime())
                .service(WynkService.fromString(details.getService()))
                .build());
    }

    private void publishNotificationToUser(String msisdn, Set<Plan> provisionedPlans, List<UserPlanDetails> activatedPlanDetails) {
        if (CollectionUtils.isNotEmpty(provisionedPlans)) {
            Offer highestHierarchyOffer = getHighestHierarchyOfferFromProvisionedSubscriptions(provisionedPlans);
            if (highestHierarchyOffer != null && highestHierarchyOffer.getMessages() != null) {
                Message message = highestHierarchyOffer.getMessages().getActivation();
                if (message.isEnabled() && StringUtils.isNotEmpty(msisdn)) {
                    Optional<UserPlanDetails> activePlanOption = activatedPlanDetails.stream().filter(u -> u.getOfferId() == highestHierarchyOffer.getId()).findAny();
                    if (activePlanOption.isPresent()) {
                        UserPlanDetails highestHierarchyPlanDetails = activePlanOption.get();
                        Plan provisionedPlan = cachingService.getPlan(highestHierarchyPlanDetails.getPlanId());
                        StandardEvaluationContext context = DefaultStandardExpressionContextBuilder.builder()
                                .variable(SubscriptionConstant.PROVISIONED_PLAN_DETAILS, highestHierarchyPlanDetails)
                                .variable(SubscriptionConstant.PROVISIONED_OFFER, highestHierarchyOffer)
                                .variable(SubscriptionConstant.PROVISIONED_PLAN, provisionedPlan)
                                .build();
                        String parsedMessage = ruleEvaluator.evaluate(message.getMessage(), () -> context, SubscriptionConstant.SUBSCRIPTION_MESSAGE_TEMPLATE_CONTEXT, String.class);
                        NotificationEvent notificationEvent = NotificationEvent.builder().message(parsedMessage).msisdn(msisdn).service(highestHierarchyOffer.getService()).priority(message.getPriority()).build();
                        eventPublisher.publishEvent(notificationEvent);
                    }
                }
            }
        }
    }

    private Offer getHighestHierarchyOfferFromProvisionedSubscriptions(Set<Plan> provisionedPlans) {
        Offer highestHierarchyOffer = null;
        int highestHierarchy = -1;
        for (Plan plan : provisionedPlans) {
            int offerId = plan.getLinkedOfferId();
            Offer offer = cachingService.getOffer(offerId);
            int hierarchy = offer.getHierarchy();
            if (hierarchy > highestHierarchy) {
                highestHierarchy = hierarchy;
                highestHierarchyOffer = offer;
            }
        }
        return highestHierarchyOffer;
    }

}
