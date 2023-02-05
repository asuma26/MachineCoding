package in.wynk.subscription.service.impl;

import in.wynk.common.dto.SessionDTO;
import in.wynk.common.enums.AppId;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.partner.common.dto.ActivePlanDetails;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.subscription.common.dto.PartnerDTO;
import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.constants.SubscriptionErrorType;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.OfferProvisionRequest;
import in.wynk.subscription.dto.request.ActiveProductsComputationRequest;
import in.wynk.subscription.dto.response.*;
import in.wynk.subscription.enums.OfferEligibilityStatus;
import in.wynk.subscription.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.*;

@Service
public class UserPlansServiceImpl implements IUserPlansService {

    private final IUserdataService userdataService;
    private final IUserProductService productService;
    private final SubscriptionCachingService cachingService;
    private final IPlanProvisionService planProvisionService;
    private final IOfferProvisionService offerProcessingService;
    private final IIngressService<EligibleBenefit> ingressService;

    public UserPlansServiceImpl(IUserdataService userdataService, IUserProductService productService, SubscriptionCachingService cachingService, IPlanProvisionService planProvisionService, IOfferProvisionService offerProcessingService, IIngressService<EligibleBenefit> ingressService) {
        this.userdataService = userdataService;
        this.productService = productService;
        this.cachingService = cachingService;
        this.planProvisionService = planProvisionService;
        this.offerProcessingService = offerProcessingService;
        this.ingressService = ingressService;
    }

    @Override
    public UserEligibleBenefits.UserEligibleBenefitsData getUserEligiblePlans(SessionDTO sessionDTO) {
        Map<Integer, EligibleBenefit> eligibleOffers = getEligibleOffers(sessionDTO);
        String packGroup = sessionDTO.get(PACK_GROUP);
        if (StringUtils.isNotBlank(packGroup) && cachingService.containsRelatedOffers(packGroup)) {
            return getUserEligiblePlans(sessionDTO.get(PACK_GROUP), eligibleOffers);
        }
        return UserEligibleBenefits.UserEligibleBenefitsData.builder().eligibleBenefits(eligibleOffers.values()).build();
    }

    @Override
    public UserCombinedBenefits getUserCombinedBenefits(SessionDTO sessionDTO) {
        UserActiveBenefits activeBenefits = getUserActiveBenefits(sessionDTO);
        UserEligibleBenefits.UserEligibleBenefitsData eligibleBenefits = getUserEligiblePlans(sessionDTO);
        return UserCombinedBenefits.builder().msisdn(sessionDTO.get(MSISDN)).activeBenefits(activeBenefits.getData().getPlans()).eligibleBenefits(eligibleBenefits.getEligibleBenefits()).build();
    }

    @Override
    public Map<UserPlanDetails, Set<Product>> getActiveUserPlanProductDetails(String uid, String service) {
        List<UserPlanDetails> activePlanDetails = userdataService.getAllUserPlanDetails(uid, service).stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).collect(Collectors.toList());
        Set<Plan> activePlans = activePlanDetails.stream().map(UserPlanDetails::getPlanId).map(cachingService::getPlan).collect(Collectors.toSet());
        ActiveProductsComputationResponse computationResponse = productService.compute(ActiveProductsComputationRequest.builder().activePlans(activePlans).build());
        return activePlanDetails.stream().filter(planDetails -> computationResponse.getActivePlanProductMap().containsKey(planDetails.getPlanId())).collect(Collectors.toMap(Function.identity(), planDetails -> computationResponse.getActivePlanProductMap().get(planDetails.getPlanId())));
    }

    @Override
    public UserActiveBenefits getUserActiveBenefits(SessionDTO sessionDTO) {
        List<UserPlanDetails> userPlans = userdataService.getAllUserPlanDetails(sessionDTO.get(UID), sessionDTO.get(SERVICE))
                .stream().filter(u -> cachingService.containsPlan(u.getPlanId())).collect(Collectors.toList());
        UserActiveBenefits.UserActivePlans.UserActivePlansBuilder builder = UserActiveBenefits.UserActivePlans.builder();
        if (CollectionUtils.isNotEmpty(userPlans)) {
            List<ActiveBenefit> freeActivePlan = userPlans.stream().filter(UserPlanDetails::isActive)
                    .filter(UserPlanDetails::isFree).map(this::convertPlans).collect(Collectors.toList());
            List<ActiveBenefit> paidPlans = userPlans.stream().filter(UserPlanDetails::isPaid).filter(userPlanDetails -> {
                Plan plan = cachingService.getPlan(userPlanDetails.getPlanId());
                return (userPlanDetails.isActive() || (userPlanDetails.isAutoRenew() && userPlanDetails.isSubscriptionInGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace())));
            }).map(this::convertPlans)
                    .collect(Collectors.toList());
            builder.addPlans(freeActivePlan);
            builder.addPlans(paidPlans);
        }
        return UserActiveBenefits.builder().data(builder.build()).build();
    }

    @Override
    public in.wynk.subscription.common.dto.ActivePlanDetails getActivePlanDetails(String uid, int planId) {
        Plan plan = cachingService.getPlan(planId);
        Optional<UserPlanDetails> planDetailsOption = userdataService.getAllUserPlanDetails(uid, plan.getService()).stream().filter(u -> u.getPlanId() == planId).findAny();
        return planDetailsOption.map(u -> in.wynk.subscription.common.dto.ActivePlanDetails.builder()
                .validFromDate(u.getStartDate())
                .validTillDate(u.getEndDate())
                .autoRenew(u.isAutoRenew())
                .planId(u.getPlanId())
                .build())
                .orElseThrow(() -> new WynkRuntimeException(SubscriptionErrorType.SUB005));
    }


    @Override
    public UserActivePlansResponse getActivePlansForUser(String uid, String service) {
        List<UserPlanDetails> userPlanDetails = userdataService.getAllUserPlanDetails(uid, service);
        List<ActivePlanDetails> activePlanDetails = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userPlanDetails)) {
            List<UserPlanDetails> activePlans = userPlanDetails.stream()
                    .filter(details -> details.getEndDate().getTime() > System.currentTimeMillis())
                    .collect(Collectors.toList());
            for (UserPlanDetails details : activePlans) {
                activePlanDetails.add(ActivePlanDetails.builder()
                        .planId(details.getPlanId())
                        .paymentChannel(details.getPaymentChannel())
                        .validTillDate(details.getEndDate())
                        .build());
            }
        }
        return UserActivePlansResponse.builder().activePlanDetails(activePlanDetails).build();
    }

    private ActiveBenefit convertPlans(UserPlanDetails userPlanDetails) {
        Plan plan = cachingService.getPlan(userPlanDetails.getPlanId());
        Offer offer = cachingService.getOffer(userPlanDetails.getOfferId());
        boolean comboPlan = StringUtils.isBlank(offer.getPackGroup());
        ActiveBenefit.ActiveBenefitBuilder activeBenefitBuilder = ActiveBenefit.builder().title(offer.getTitle()).subtitle(offer.getSubtitle()).button(offer.getButton()).packGroup(offer.getPackGroup())
                .autoRenew(userPlanDetails.isAutoRenew()).colorCode(offer.getColorCode()).validityUnit(plan.getPeriod().getValidityUnit()).displayAmount(plan.getPrice().getDisplayAmount()).total(plan.getPrice().getAmount()).planType(userPlanDetails.getPlanType())
                .meta(offer.getMeta()).combo(comboPlan).offerId(offer.getId()).offerHierarchy(offer.getHierarchy()).planId(plan.getId()).validTill(userPlanDetails.isAutoRenew() && !userPlanDetails.isActive() ? userPlanDetails.getValidTillWithGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace()) : userPlanDetails.getEndDate().getTime()).descriptions(offer.getDescriptions());
        if (comboPlan) {
            List<PartnerDTO> partnerDTOS = getPartnerDTOs(offer);
            activeBenefitBuilder.partners(partnerDTOS);
        } else {
            Partner partner = cachingService.getPartner(offer.getPackGroup());
            activeBenefitBuilder.colorCode(partner.getColorCode());
            activeBenefitBuilder.partnerName(partner.getName());
            activeBenefitBuilder.partnerLogo(partner.getLogo());
            activeBenefitBuilder.partnerIcon(partner.getIcon());
        }
        return activeBenefitBuilder.build();
    }

    private UserEligibleBenefits.UserEligibleBenefitsData getUserEligiblePlans(String packGroup, Map<Integer, EligibleBenefit> eligibleOffers) {
        List<Integer> relatedOfferIds = cachingService.getRelatedOffersIds(packGroup);
        List<EligibleBenefit> eligibleBenefits = eligibleOffers.keySet().stream()
                .filter(relatedOfferIds::contains).map(eligibleOffers::get).collect(Collectors.toList());
        return UserEligibleBenefits.UserEligibleBenefitsData.builder().eligibleBenefits(eligibleBenefits).build();
    }

    private Map<Integer, EligibleBenefit> getEligibleOffers(SessionDTO sessionDTO) {
        OfferProvisionRequest offerProvisionRequest = createProvisionRequest(sessionDTO);
        //get all eligible paid offers
        List<OfferCheckEligibility> eligibilityList = offerProcessingService.getEligibleOffers(offerProvisionRequest);
        List<Offer> eligiblePaidOffers = eligibilityList.stream()
                .filter(this::offerEligibleForPurchase).map(OfferCheckEligibility::getOffer).collect(Collectors.toList());
        //get all active offer to plan mapping
        Map<Integer, Integer> activeOfferPlanMap = eligibilityList.stream().filter(this::activePaidOffers)
                .collect(Collectors.toMap(o -> o.getOffer().getId(), OfferCheckEligibility::getActivePlanId));
        List<UserPlanDetails> userPlanDetailsList = userdataService.getAllUserPlanDetails(sessionDTO.get(UID), sessionDTO.get(SERVICE));
        sessionDTO.put(ELIGIBLE_PLANS, new HashSet<>());
        return eligibleOfferPlansForUser(sessionDTO, userPlanDetailsList, eligiblePaidOffers, activeOfferPlanMap);
    }

    private Map<Integer, EligibleBenefit> eligibleOfferPlansForUser(SessionDTO sessionDTO, List<UserPlanDetails> userPlanDetailsList, List<Offer> eligiblePaidOffers, Map<Integer, Integer> activeOfferPlanMap) {
        //map eligible offers if any.
        List<EligibleBenefit> eligibleBenefits = eligiblePaidOffers.stream().sorted((o1, o2) -> (o2.getDisplayOrder() == o1.getDisplayOrder()) ? (o2.getHierarchy() - o1.getHierarchy()) : (o1.getDisplayOrder() - o2.getDisplayOrder())).map(o -> getEligibleBenefits(sessionDTO, userPlanDetailsList, o, activeOfferPlanMap)).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(eligibleBenefits)) {
            if (StringUtils.isNotBlank(sessionDTO.get(INGRESS_INTENT)))
                ingressService.decorate(sessionDTO.get(INGRESS_INTENT), eligibleBenefits.get(0));
        }
        return eligibleBenefits.stream().collect(Collectors.toMap(EligibleBenefit::getId, Function.identity(), (o, n) -> n, LinkedHashMap::new));
    }

    private boolean offerEligibleForPurchase(OfferCheckEligibility eligibilityResponse) {
        return eligibilityResponse.getStatus() == OfferEligibilityStatus.ELIGIBLE &&
                eligibilityResponse.getOffer().getProvisionType() == ProvisionType.PAID;
    }

    private boolean activePaidOffers(OfferCheckEligibility eligibilityResponse) {
        return eligibilityResponse.getOffer().getProvisionType() == ProvisionType.PAID && eligibilityResponse.getStatus() == OfferEligibilityStatus.ACTIVE;
    }

    private EligibleBenefit getEligibleBenefits(SessionDTO sessionDTO, List<UserPlanDetails> userPlanDetailsList, Offer offer, Map<Integer, Integer> activeOfferPlanMap) {
        List<AvailablePlans> availablePlans = getEligibleBenefits(sessionDTO, userPlanDetailsList, offer.getId(), activeOfferPlanMap.getOrDefault(offer.getId(), 0));
        if (CollectionUtils.isEmpty(availablePlans)) {
            return null;
        }
        Integer startingRate = availablePlans.stream().map(AvailablePlans::getPerMonthValue).reduce(Math::min).orElse(null);
        EligibleBenefit.EligibleBenefitBuilder eligibleBenefitBuilder = EligibleBenefit.builder().id(offer.getId()).plans(availablePlans).displayOrder(offer.getDisplayOrder()).hierarchy(offer.getHierarchy()).startingRate(startingRate).combo(true)
                .comboHeader(offer.getComboHeader()).colorCode(offer.getColorCode()).planStartTitle(offer.getTitle()).title(offer.getTitle())
                .meta(offer.getMeta()).subtitle(offer.getDescription()).button(offer.getButton()).descriptions(offer.getDescriptions());
        String packGroup = StringUtils.isNotBlank(offer.getPackGroup()) ? offer.getPackGroup() : DEFAULT_PACK_GROUP.concat(offer.getService().toLowerCase());

        Partner partner = cachingService.getPartner(packGroup);
        eligibleBenefitBuilder.colorCode(partner.getColorCode());
        eligibleBenefitBuilder.packGroup(offer.getPackGroup());
        eligibleBenefitBuilder.partnerName(partner.getName());
        eligibleBenefitBuilder.partnerLogo(partner.getLogo());
        eligibleBenefitBuilder.partnerIcon(partner.getIcon());
        eligibleBenefitBuilder.partnerContentImages(partner.getContentImages());
        if (offer.isCombo()) {
            List<PartnerDTO> partnerDTOS = getPartnerDTOs(offer);
            eligibleBenefitBuilder.partners(partnerDTOS);
        } else {
            eligibleBenefitBuilder.combo(false);
        }
        return eligibleBenefitBuilder.build();
    }

    private List<PartnerDTO> getPartnerDTOs(Offer offer) {
        return offer.getProducts().values().stream()
                .map(cachingService::getPartner).map(this::fromPartner).collect(Collectors.toList());
    }

    private PartnerDTO fromPartner(Partner partner) {
        return PartnerDTO.builder()
                .partnerLogo(partner.getLogo())
                .partnerIcon(partner.getIcon())
                .name(partner.getName())
                .portrait(partner.getPortrait())
                .colorCode(partner.getColorCode())
                .description(partner.getDescription())
                .packGroup(partner.getPackGroup())
                .build();
    }

    private List<AvailablePlans> getEligibleBenefits(SessionDTO sessionDTO, List<UserPlanDetails> userPlanDetailsList, Integer offerId, Integer currentPlanId) {
        List<Plan> availablePlans = planProvisionService.getEligiblePlans(offerId, AppId.fromString(sessionDTO.get(APP_ID)), userPlanDetailsList);
        List<Integer> availablePlanIds = availablePlans.stream().map(Plan::getId).collect(Collectors.toList());
        List<Integer> availableFreeTrialPlanIds = availablePlans.stream().map(Plan::getLinkedFreePlanId).filter(availablePlanIds::contains).collect(Collectors.toList());
        Set<Integer> eligiblePlans = sessionDTO.get(ELIGIBLE_PLANS);
        eligiblePlans.addAll(availablePlanIds);
        int currentPlanHierarchy = currentPlanId == 0 ? 0 : cachingService.getPlan(currentPlanId).getHierarchy();
        return availablePlans.stream()
                .filter(p -> (p.getHierarchy() > currentPlanHierarchy && p.getType() != PlanType.FREE_TRIAL))
                .map(plan -> convertedPlan(plan, availableFreeTrialPlanIds.contains(plan.getLinkedFreePlanId())))
                .collect(Collectors.toList());
    }

    private AvailablePlans convertedPlan(Plan plan, boolean freeTrialAvailable) {
        return AvailablePlans.builder()
                .sku(plan.getSku())
                .planId(plan.getId())
                .month(plan.getPeriod().getMonth())
                .total(plan.getPrice().getAmount())
                .discount(plan.getPrice().getSavings())
                .freeTrialAvailable(freeTrialAvailable)
                .title(plan.getTitle())
                .isBestValue(plan.getPrice().isBestPlan())
                .validityUnit(plan.getPeriod().getValidityUnit())
                .perMonthValue(plan.getPrice().getMonthlyAmount())
                .displayAmount(plan.getPrice().getDisplayAmount())
                .build();
    }

    private OfferProvisionRequest createProvisionRequest(SessionDTO sessionDTO) {
        String uid = sessionDTO.get(UID);
        String appVersion = sessionDTO.get(APP_VERSION);
        Integer buildNo = sessionDTO.get(BUILD_NO);
        String msisdn = MsisdnUtils.normalizePhoneNumber(sessionDTO.get(MSISDN));
        String os = sessionDTO.get(OS);
        String deviceId = sessionDTO.get(DEVICE_ID);
        String appId = sessionDTO.get(APP_ID);
        String service = sessionDTO.get(SERVICE);
        return OfferProvisionRequest.builder().appId(appId).service(service).appVersion(appVersion).buildNo(buildNo).deviceId(deviceId)
                .msisdn(msisdn).os(os).uid(uid).build();
    }

}
