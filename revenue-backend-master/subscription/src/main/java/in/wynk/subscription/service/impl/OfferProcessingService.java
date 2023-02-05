package in.wynk.subscription.service.impl;

import com.datastax.driver.core.utils.UUIDs;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.BeanLocatorFactory;
import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.core.service.IUsermetaService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.*;
import in.wynk.subscription.dto.response.MultiPlanProvisioningResponse;
import in.wynk.subscription.dto.response.OfferProvisionResponse;
import in.wynk.subscription.enums.OfferEligibilityStatus;
import in.wynk.subscription.enums.OfferEligibilityStatusReason;
import in.wynk.subscription.service.*;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import in.wynk.vas.client.enums.UserType;
import in.wynk.vas.client.service.VasClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.util.Pair;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.*;
import static in.wynk.subscription.enums.OfferEligibilityStatus.ACTIVE;
import static in.wynk.subscription.enums.OfferEligibilityStatus.ELIGIBLE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class OfferProcessingService implements IOfferProvisionService {

    private final VasClientService vasClientService;
    private final SubscriptionCachingService cachingService;
    private final ApplicationContext appContext;
    private final IUsermetaService usermetaService;
    private final IUserdataService userdataService;
    private final IUserPlanManager userPlanManager;
    private final IPlanProvisionService planProvisionService;

    public OfferProcessingService(VasClientService vasClientService, SubscriptionCachingService cachingService, ApplicationContext appContext, IUsermetaService usermetaService, IUserdataService userdataService, IUserPlanManager userPlanManager, IPlanProvisionService planProvisionService) {
        this.vasClientService = vasClientService;
        this.cachingService = cachingService;
        this.appContext = appContext;
        this.usermetaService = usermetaService;
        this.userdataService = userdataService;
        this.userPlanManager = userPlanManager;
        this.planProvisionService = planProvisionService;
    }

    public OfferProvisionResponse.OfferProvisionData provisionOffers(OfferProvisionRequest offerProvisionRequest) {
        String uid = offerProvisionRequest.getUid();
        WynkService service = offerProvisionRequest.getService();
        OfferEligibilityCheckRequest eligibilityRequest = buildEligibilityRequest(offerProvisionRequest);
        OfferEligibilityCheckResponse eligibilityResponse = checkEligibility(eligibilityRequest);
        List<Integer> planIdsToBeSubscribed = eligibilityResponse.getFreeEligibilityResponse().stream().map(OfferCheckEligibility::getOffer).map(Offer::getFreePlan).collect(Collectors.toList());
        PlanProvisioningRequest request = MultiPlanProvisionRequest.builder().service(service).paymentCode(FREE).uid(uid).planIds(planIdsToBeSubscribed).paymentPartner(WYNK).userPlanDetails(eligibilityRequest.getUserPlanDetails()).build();
        MultiPlanProvisioningResponse provisionedSubscriptions = (MultiPlanProvisioningResponse) userPlanManager.subscribe(request);
        List<SubscriptionStatus> subscriptionStatus = provisionedSubscriptions.getProvisionedPlanProductsDetails().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(p -> Pair.of(entry.getKey(), p))).map(SubscriptionStatus::new).collect(Collectors.toList());
        List<OfferStatus> offerStatus = getOfferStatus(eligibilityRequest, eligibilityResponse, provisionedSubscriptions.getProvisionedPlanProductsDetails(), eligibilityRequest.getUserPlanDetails());
        //TODO: remove this and get actual plans that is provisioned
        Set<Plan> plans = provisionedSubscriptions.getProvisionedPlanProductsDetails().keySet().stream().map(UserPlanDetails::getPlanId).map(cachingService::getPlan).collect(Collectors.toSet());
        updateOfferRelatedStatus(offerProvisionRequest, plans);
        Userdata userdata = getUserData(eligibilityRequest);
        return OfferProvisionResponse.OfferProvisionData.builder().subscriptionStatus(subscriptionStatus)
                .offerStatus(offerStatus).userdata(userdata).build();
    }

    private void updateOfferRelatedStatus(OfferProvisionRequest request, Set<Plan> subscribedPlans) {
        if (CollectionUtils.isNotEmpty(subscribedPlans)) {
            final UUID id = UUIDs.timeBased();
            CompletableFuture.runAsync(() -> updateOfferDeviceStatus(request.getMsisdn(), request.getService().getValue(), request.getDeviceId(), subscribedPlans, id));
        }
    }

    private Userdata getUserData(OfferEligibilityCheckRequest offerEligibilityCheckRequest) {
        Map<String, List<ThanksUserSegment>> allThanksSegment = offerEligibilityCheckRequest.getAllThanksSegment();
        Map<String, List<String>> thanksSegment = new HashMap<>();
        for (String si : allThanksSegment.keySet()) {
            List<String> servicePacks = allThanksSegment.get(si).stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toList());
            thanksSegment.put(si, servicePacks);
        }
        return Userdata.builder().thanksSegment(thanksSegment).build();
    }

    private List<OfferStatus> getOfferStatus(OfferEligibilityCheckRequest offerEligibilityCheckRequest, OfferEligibilityCheckResponse offerEligibilityCheckResponse, Map<UserPlanDetails, Set<Product>> provisionedPlanProductsDetails, List<UserPlanDetails> userPlanDetailsList) {
        List<OfferStatus> offerStatus = getOfferStatusForFreeOffers(provisionedPlanProductsDetails.keySet());
        paidOffersEligibilityReevaluation(offerEligibilityCheckResponse, provisionedPlanProductsDetails);
        offerStatus.addAll(getOfferStatusForPaidOffers(offerEligibilityCheckRequest, offerEligibilityCheckResponse, userPlanDetailsList));
        return offerStatus;
    }

    private List<OfferStatus> getOfferStatusForPaidOffers(OfferEligibilityCheckRequest offerEligibilityCheckRequest, OfferEligibilityCheckResponse offerEligibilityCheckResponse, List<UserPlanDetails> userPlanDetailsList) {
        return offerEligibilityCheckResponse.getOfferEligibilityStatus().stream().filter(eligibilityStatus -> eligibilityStatus.getStatus() == ELIGIBLE).map(OfferCheckEligibility::getOffer).filter(Offer::isPaid).map(offer -> createEligibleOfferStatus(offerEligibilityCheckRequest, offer, userPlanDetailsList)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void paidOffersEligibilityReevaluation(OfferEligibilityCheckResponse offerEligibilityCheckResponse, Map<UserPlanDetails, Set<Product>> provisionedPlanProductsDetails) {
        Map<String, Integer> provisionedProduct = provisionedPlanProductsDetails.values().stream().flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toMap(Product::getPackGroup, Product::getHierarchy));
        for (OfferCheckEligibility offerCheckEligibility : offerEligibilityCheckResponse.getOfferEligibilityStatus()) {
            Offer offer = offerCheckEligibility.getOffer();
            if (offerCheckEligibility.getStatus() == ELIGIBLE && offer.getProvisionType() == ProvisionType.PAID) {
                log.debug("evaluating paid offer id: {}", offer.getId());
                Map<String, Integer> availableProduct = offer.getProducts().keySet().stream().map(Integer::parseInt)
                        .map(cachingService::getProduct).filter(Objects::nonNull).collect(Collectors.toMap(Product::getPackGroup, Product::getHierarchy));
                Optional<Map.Entry<String, Integer>> any = provisionedProduct.entrySet().stream()
                        .filter(pg -> availableProduct.containsKey(pg.getKey())).filter(pg -> pg.getValue() < availableProduct.remove(pg.getKey())).findAny();
                if (!any.isPresent() && MapUtils.isEmpty(availableProduct)) {
                    log.info("paid offer eligibility for id: {} status: NOT_ELIGIBLE reason: ALL_PRODUCTS_ALREADY_PROVISIONED", offer.getId());
                    offerCheckEligibility.setEligible(false);
                    offerCheckEligibility.setStatus(OfferEligibilityStatus.NOT_ELIGIBLE);
                    offerCheckEligibility.setReason(OfferEligibilityStatusReason.ALL_PRODUCTS_ALREADY_PROVISIONED);
                }
            }
        }
    }

    private List<OfferStatus> getOfferStatusForFreeOffers(Collection<UserPlanDetails> planDetails) {
        List<OfferStatus> freeOffersStatus = new ArrayList<>();
        Set<Integer> offers = new HashSet<>();
        for (UserPlanDetails provisionedPlanDetail : planDetails) {
            int offerId = provisionedPlanDetail.getOfferId();
            if (!offers.contains(offerId) && cachingService.containsOffer(offerId)) {
                offers.add(offerId);
                OfferStatus status = createActiveOfferStatus(provisionedPlanDetail);
                freeOffersStatus.add(status);
            }
        }
        return freeOffersStatus;
    }

    private OfferStatus createActiveOfferStatus(UserPlanDetails planDetails) {
        int offerId = planDetails.getOfferId();
        Offer offer = cachingService.getOffer(offerId);
        Plan plan = cachingService.getPlan(planDetails.getPlanId());
        return ActiveOfferStatus.builder()
                .validTill(planDetails.isAutoRenew() ? planDetails.getValidTillWithGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace()) : planDetails.getEndDate().getTime())
                .autoRenew(planDetails.isAutoRenew())
                .planIds(Collections.singletonList(planDetails.getPlanId()))
                .type(offer.getProvisionType())
                .title(offer.getTitle())
                .offerId(offer.getId())
                .status(ACTIVE.name())
                .build();
    }

    private OfferStatus createEligibleOfferStatus(OfferEligibilityCheckRequest offerEligibilityCheckRequest,Offer offer, List<UserPlanDetails> userPlanDetailsList) {
        List<Plan> eligiblePlans = planProvisionService.getEligiblePlans(offer.getId(), offerEligibilityCheckRequest.getAppId(), userPlanDetailsList);
        if(CollectionUtils.isEmpty(eligiblePlans)) {
            return null;
        }
        List<Integer> planIds = eligiblePlans.stream().map(Plan::getId).collect(Collectors.toList());
        Optional<Plan> freeTrialPlan = eligiblePlans.stream().filter(plan -> plan.getType() == PlanType.FREE_TRIAL).findAny();
        if (freeTrialPlan.isPresent()) {
            planIds.remove((Integer) freeTrialPlan.get().getId());
        }
        return EligibleOfferStatus.builder()
                .planIds(planIds)
                .offerId(offer.getId())
                .title(offer.getTitle())
                .status(ELIGIBLE.name())
                .type(offer.getProvisionType())
                .freeTrialAvailable(freeTrialPlan.isPresent())
                .build();
    }

    private OfferEligibilityCheckRequest buildEligibilityRequest(OfferProvisionRequest offerProvisionRequest) {
        String msisdn = offerProvisionRequest.getMsisdn();
        WynkService service = offerProvisionRequest.getService();
        MsisdnOperatorDetails msisdnOperatorDetails = vasClientService.allOperatorDetails(msisdn);
        MultiValuedMap<UserType, String> allUserTypes = msisdnOperatorDetails.getAllUserTypes();
        List<UserPlanDetails> allPlanDetails = userdataService.getAllUserPlanDetails(offerProvisionRequest.getUid(), service.getValue(), msisdn);
        Map<Integer, Integer> activeOfferToPlanMap = new HashMap<>();
        for (UserPlanDetails userPlanDetails : allPlanDetails) {
            if (userPlanDetails.isFreeActiveOrPaidActive()) {
                if (activeOfferToPlanMap.containsKey(userPlanDetails.getOfferId())) {
                    int oldPlanId = activeOfferToPlanMap.get(userPlanDetails.getOfferId());
                    Plan oldPlan = cachingService.getPlan(oldPlanId);
                    int newPlanId = userPlanDetails.getPlanId();
                    Plan newPlan = cachingService.getPlan(newPlanId);
                    activeOfferToPlanMap.put(userPlanDetails.getOfferId(), oldPlan.getHierarchy() > newPlan.getHierarchy() ? oldPlanId : newPlanId);
                } else {
                    activeOfferToPlanMap.put(userPlanDetails.getOfferId(), userPlanDetails.getPlanId());
                }
            }
        }
        Map<String, List<ThanksUserSegment>> allThanksUserSegments = usermetaService.getAllThankSegments(msisdn, new HashSet<>(allUserTypes.values()));
        return OfferEligibilityCheckRequest.builder()
                .msisdnOperatorDetails(msisdnOperatorDetails)
                .deviceId(offerProvisionRequest.getDeviceId())
                .buildNo(offerProvisionRequest.getBuildNo())
                .activeOfferToPlanMap(activeOfferToPlanMap)
                .allThanksSegment(allThanksUserSegments)
                .appId(offerProvisionRequest.getAppId())
                .os(offerProvisionRequest.getOs())
                .userPlanDetails(allPlanDetails)
                .wynkService(service)
                .msisdn(msisdn)
                .build();
    }

    private void updateOfferDeviceStatus(String msisdn, String service, String deviceId, Set<Plan> plans, UUID id) {
        Set<OfferDeviceMapping> allOfferDeviceMappings = plans.stream()
                .map(plan -> OfferDeviceMapping.builder()
                        .deviceId(deviceId)
                        .planId(plan.getId())
                        .id(id)
                        .service(plan.getService())
                        .offerId(plan.getLinkedOfferId())
                        .build()).collect(Collectors.toSet());
        userdataService.addAllOfferDeviceMapping(msisdn, service, deviceId, allOfferDeviceMappings);
    }

    public List<OfferCheckEligibility> getEligibleOffers(OfferProvisionRequest offerProvisionRequest) {
        OfferEligibilityCheckRequest offerEligibilityCheckRequest = buildEligibilityRequest(offerProvisionRequest);
        OfferEligibilityCheckResponse offerEligibilityCheckResponse = checkEligibility(offerEligibilityCheckRequest);
        Map<UserPlanDetails, Set<Product>> activeUserPlanProductDetails = BeanLocatorFactory.getBean(IUserPlansService.class).getActiveUserPlanProductDetails(offerProvisionRequest.getUid(), offerProvisionRequest.getService().getValue());
        //add post processing check
        paidOffersEligibilityReevaluation(offerEligibilityCheckResponse, activeUserPlanProductDetails);
        return offerEligibilityCheckResponse.getOfferEligibilityStatus();
    }

    @Deprecated
    private Map<Integer, Integer> getActiveOffersWithPlan(List<Subscription> allSubscriptions) {
        return allSubscriptions.stream()
                .filter(s -> s.isSubscriptionActive() || s.isPaidActiveOrInGrace())
                .filter(s -> StringUtils.isNotEmpty(getMetaObjectFromSubscription(s, OFFER_ID)) && StringUtils.isNotEmpty(getMetaObjectFromSubscription(s, PLAN_ID)))
                .collect(Collectors.groupingBy(s -> Pair.of(Integer.parseInt(getMetaObjectFromSubscription(s, OFFER_ID)), Integer.parseInt(getMetaObjectFromSubscription(s, PLAN_ID))),
                        Collectors.mapping(Subscription::getProductId, Collectors.toSet())))
                .entrySet()
                .stream()
                .filter(entry -> cachingService.getOffer(entry.getKey().getFirst()) != null && MapUtils.isNotEmpty(cachingService.getOffer(entry.getKey().getFirst()).getProducts()))
                .filter(entry -> cachingService.getOffer(entry.getKey().getFirst()).getProducts().keySet().containsAll(entry.getValue().stream().map(String::valueOf).collect(Collectors.toList())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (o, n) -> o, HashMap::new));
    }

    private String getMetaObjectFromSubscription(Subscription subscription, String key) {
        Map<String, String> meta = subscription.getPaymentMetaData();
        String value = StringUtils.EMPTY;
        if (!isEmpty(meta)) {
            value = meta.getOrDefault(key, StringUtils.EMPTY);
        }
        return value;
    }

    private OfferEligibilityCheckResponse checkEligibility(OfferEligibilityCheckRequest offerEligibilityCheckRequest, List<Offer> offerBucket) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(appContext));
        List<OfferCheckEligibility> offerEligibilityStatus = new ArrayList<>();
        OfferContext offerContext = new OfferContext(offerEligibilityCheckRequest);
        for (Offer offer : offerBucket) {
            offerContext.setOffer(offer);
            context.setVariable("offerContext", offerContext);
            Expression ruleExpression = cachingService.getOfferRuleExpressions().get(offer.getId());
            OfferCheckEligibility response = (OfferCheckEligibility) ruleExpression.getValue(context);
            if (response == null) {
                log.error("Unable to fetch offer check eligibility for offer id: {}", offer.getId());
                continue;
            }
            log.info("offer eligibility for id: {} status: {} reason: {} ", offer.getId(), response.getStatus().name(), response.getReason());
            offerEligibilityStatus.add(response);
        }
        return OfferEligibilityCheckResponse.builder().offerEligibilityStatus(offerEligibilityStatus).build();
    }

    public OfferEligibilityCheckResponse checkEligibility(OfferEligibilityCheckRequest offerEligibilityCheckRequest) {
        List<Offer> offerBucket = cachingService.getServiceOffers(offerEligibilityCheckRequest.getWynkService());
        return checkEligibility(offerEligibilityCheckRequest, offerBucket);
    }

}
