package in.wynk.subscription.core.service;

import com.github.annotation.analytic.core.service.AnalyticService;
import com.google.gson.Gson;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.dao.repository.filterUsers.FilterDbDao;
import in.wynk.subscription.core.events.RenewalMigrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.*;
import static in.wynk.subscription.core.constants.SubscriptionLoggingMarkers.MIGRATION_ERROR;

@Slf4j
@Service
public class MigrationService {

    private static final String MIGRATION_DB = "migration";
    private final UserdataService userdataService;
    private final SubscriptionCachingService cachingService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Gson gson;
    private final FilterDbDao filterDbDao;

    public MigrationService(Gson gson, UserdataService userdataService, SubscriptionCachingService cachingService, ApplicationEventPublisher applicationEventPublisher, FilterDbDao filterDbDao) {
        this.userdataService = userdataService;
        this.cachingService = cachingService;
        this.gson = gson;
        this.applicationEventPublisher = applicationEventPublisher;
        this.filterDbDao = filterDbDao;
    }

    public List<UserPlanDetails> migrateFromSubscriptionToUserPlan(List<Subscription> subscriptions) {
        return subscriptions.stream().filter(s -> MapUtils.isNotEmpty(s.getPaymentMetaData()) && s.getPaymentMetaData().containsKey(PLAN_ID)).collect(Collectors.toMap(subs -> subs.getPaymentMetaData().get(PLAN_ID), Function.identity(), (f, s) -> f)).values().stream().map(this::from).collect(Collectors.toList());
    }

    private UserPlanDetails from(Subscription subscription) {
        return migrateForMusic(subscription);
    }

    public List<UserPlanDetails> migrateUserPlanDetails(String uid, String service, String msisdn, List<UserPlanDetails> userPlanDetailsList) {
        return migrateUserPlanDetailsIfOld(uid, service, msisdn, userPlanDetailsList);
    }

    @Deprecated
    public List<Subscription> migrateSubscriptions(String uid, String service, List<Subscription> subscriptions) {
        return migrateSubscriptionsIfOld(uid, service, subscriptions);
    }

    private List<UserPlanDetails> migrateUserPlanDetailsIfOld(String uid, String service, String msisdn, List<UserPlanDetails> userPlanDetailsList) {
        List<OfferPlanMapping> offerPlanMappingIds = userPlanDetailsList.parallelStream().map((userPlanDetails) -> OfferPlanMapping.builder().newOfferId(userPlanDetails.getOfferId()).newPlanId(userPlanDetails.getPlanId()).build()).collect(Collectors.toList());
        Map<String, Map<Integer, OfferPlanMapping>> oldOfferIdToNewOfferPlanMapping = cachingService.getOldOfferIdToNewOfferPlanMapping();
        if (!CollectionUtils.isEmpty(oldOfferIdToNewOfferPlanMapping)) {
            Map<Integer, OfferPlanMapping> offerToOfferPlan = oldOfferIdToNewOfferPlanMapping.get(service);
            if (!CollectionUtils.isEmpty(offerToOfferPlan)) {
                List<OfferPlanMapping> offerPlanMappingIdsRemaining = offerToOfferPlan.values().parallelStream().filter(x -> !offerPlanMappingIds.contains(x)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(offerPlanMappingIdsRemaining)) {
                    try {
                        Map<Integer, OfferPlanMapping> offerToOfferPlanRemaining = offerToOfferPlan
                                .entrySet()
                                .parallelStream()
                                .filter(map -> offerPlanMappingIdsRemaining.contains(map.getValue()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                        List<UserPlanDetails> migratedUserPlanDetailsList = migrateUserPlanDetail(uid, service, msisdn, offerToOfferPlanRemaining);
                        if (!CollectionUtils.isEmpty(migratedUserPlanDetailsList)) {
                            userdataService.addAllUserPlanDetails(uid, service, migratedUserPlanDetailsList);
                        }
                        userPlanDetailsList.addAll(migratedUserPlanDetailsList);
                    } catch (Exception e) {
                        log.error(MIGRATION_ERROR, "Unable to migrate User Plan Details with uid: {}", uid, e);
                    }
                }
            }
        }
        return userPlanDetailsList;
    }

    @Deprecated
    private List<Subscription> migrateSubscriptionsIfOld(String uid, String service, List<Subscription> existingSubscriptions) {
        List<Subscription> subscriptions = new ArrayList<>();
        List<Subscription> freeSubsForMigration = existingSubscriptions.stream().filter(Subscription::isFree).
                filter(s -> cachingService.isFreePackEligibleForMigration(s.getPartnerProductId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(freeSubsForMigration)) {
            //Migrate Free eligible subscription for Music
            for (Subscription subscription : freeSubsForMigration) {
                if (cachingService.getOldFMFToNewPlanMapping().containsKey(subscription.getPartnerProductId())) {
                    List<Subscription> fmfSubscription = migrateFMFSubscription(uid, service, subscription, cachingService.getOldFMFToNewPlanMapping().get(subscription.getPartnerProductId()));
                    if(!CollectionUtils.isEmpty(fmfSubscription)){
                        subscriptions.addAll(fmfSubscription);
                    }
                } else if (filterDbDao.getFilterUserDetails(uid, MIGRATION_DB) != null) {
                    List<Subscription> freeSubscription = migrateFreeSubscription(uid, service, subscription, cachingService.getOldFreePackToNewPlanMapping().get(subscription.getPartnerProductId()));
                    if(!CollectionUtils.isEmpty(freeSubscription)){
                        subscriptions.addAll(freeSubscription);
                    }
                }
            }
        }
        existingSubscriptions = existingSubscriptions.parallelStream().filter(s -> !s.isFree()).filter(this::isEligibleForMigration).collect(Collectors.toList());
        AnalyticService.update(OLD_SUBS_FOR_MIGRATION, gson.toJson(existingSubscriptions));
        for (Subscription subscription : existingSubscriptions) {
            try {
                List<Subscription> migratedSubs = migrateSubscription(uid, service, subscription);
                if (!CollectionUtils.isEmpty(migratedSubs)) {
                    subscriptions.addAll(migratedSubs);
                }
            } catch (Exception e) {
                log.error(MIGRATION_ERROR, "Unable to migrate subscription: {}", subscription);
            }
        }

        // since music paid subscription doesn't have offer msisdn mapping we will migrate music user plan details from here
        if (WynkService.fromString(service) == WynkService.MUSIC) {
            List<UserPlanDetails> migratedPlanDetails = subscriptions.stream().map(this::migrateForMusic).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(migratedPlanDetails)) {
                userdataService.addAllUserPlanDetails(uid, service, migratedPlanDetails);
            }
            migratedPlanDetails.stream()
                    .filter(UserPlanDetails::isAutoRenew)
                    .filter(UserPlanDetails::isPaidActiveOrInGrace)
                    .filter(planDetails -> PlanType.fromString(planDetails.getPlanType()) == PlanType.SUBSCRIPTION)
                    .forEach(planDetails -> applicationEventPublisher.publishEvent(RenewalMigrationEvent.builder()
                            .paymentCode(planDetails.getPaymentCode())
                            .nextChargingDate(planDetails.getEndDate())
                            .paymentMetaData(planDetails.getMeta())
                            .uid(planDetails.getUid())
                            .build()));
        }

        return subscriptions;
    }

    private List<Subscription> migrateFreeSubscription(String uid, String service, Subscription subscription, Integer planId) {
        if(cachingService.containsPlan(planId)){
            Plan plan = cachingService.getPlan(planId);
            if(cachingService.containsOffer(plan.getLinkedOfferId())){
                Offer offer = cachingService.getOffer(plan.getLinkedOfferId());
                updateMetadataForOldSubscription(subscription, offer, plan);
                return provisionFreeOffer(uid, service, offer, plan, subscription);
            }
        }
        return Collections.emptyList();
    }

    private List<Subscription> migrateFMFSubscription(String uid, String service, Subscription subscription, Integer planId) {
        if(cachingService.containsPlan(planId)){
            Plan plan = cachingService.getPlan(planId);
            if(cachingService.containsOffer(plan.getLinkedOfferId())){
                Offer offer = cachingService.getOffer(plan.getLinkedOfferId());
                updateMetadataForOldSubscription(subscription, offer, plan);
                return provisionOffer(uid, service, offer, subscription);
            }
        }
        return Collections.emptyList();
    }

    private String getPaymentCode(int productId) {
        String paymentMethod = cachingService.getSubscriptionPackMap().get(productId);
        return StringUtils.equalsIgnoreCase(paymentMethod, "se") ? "AirtelCarrierBilling" : paymentMethod;
    }

    private List<UserPlanDetails> migrateUserPlanDetail(String uid, String service, String msisdn, Map<Integer, OfferPlanMapping> offerToOfferPlanRemaining) {
        Map<Integer, Long> offerIdFrequencyCount = userdataService.getAllOfferMsisdnMapping(service, msisdn)
                .parallelStream()
                .map(OfferMsisdnMapping::getOfferId)
                .filter(offerToOfferPlanRemaining::containsKey)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<UserPlanDetails> userPlanDetailsList = new ArrayList<>();
        for (Map.Entry<Integer, Long> offerIdCount : offerIdFrequencyCount.entrySet()) {
            OfferPlanMapping newOfferPlanMappingId = offerToOfferPlanRemaining.get(offerIdCount.getKey());
            userPlanDetailsList.add(migrate(uid, service, newOfferPlanMappingId.getNewOfferId(), newOfferPlanMappingId.getNewPlanId(), offerIdCount.getValue()));
        }
        return userPlanDetailsList;
    }

    private List<Subscription> migrateSubscription(String uid, String service, Subscription subscription) {
        List<Subscription> newSubscriptions = null;
        Map<Integer, Integer> oldPackToNewPlanMapping = cachingService.getOldPackToNewPlanMapping();
        if (!CollectionUtils.isEmpty(oldPackToNewPlanMapping)) {
            Integer planId = oldPackToNewPlanMapping.get(subscription.getPartnerProductId());
            if (planId != null && cachingService.containsPlan(planId)) {
                // TODO:: RG-1076 force migration hack for music is applied, kindly remove once migration is no longer required
                if (subscription.getService().equalsIgnoreCase(WynkService.MUSIC.getValue()) &&
                        (StringUtils.isNotEmpty(subscription.getPaymentMethod()) &&
                        subscription.getPaymentMethod().equalsIgnoreCase(ITUNES)) &&
                        subscription.getPartnerProductId() == 11000 &&
                        subscription.getProductId() == 110005) {
                    planId = 1101;
                }
                Plan plan = cachingService.getPlan(planId);
                if ((subscription.getAutoRenewalOff() && subscription.isSubscriptionActive()) || (!subscription.getAutoRenewalOff() && subscription.isSubscriptionInGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace()))) {
                    if (cachingService.containsOffer(plan.getLinkedOfferId())) {
                        Offer offer = cachingService.getOffer(plan.getLinkedOfferId());
                        updateMetadataForOldSubscription(subscription, offer, plan);
                        newSubscriptions = provisionOffer(uid, service, offer, subscription);
                    }
                }
            }
        }
        return newSubscriptions;
    }

    public boolean isEligibleForMigration(Subscription subscription) {
        Map<String, String> metaData = subscription.getPaymentMetaData();
        return !metaData.containsKey(MIGRATED) || !metaData.get(MIGRATED).equalsIgnoreCase(TRUE);
    }

    private void updateMetadataForOldSubscription(Subscription subscription, Offer offer, Plan plan) {
        Map<String, String> metaData = subscription.getPaymentMetaData();
        metaData.put(MIGRATED, TRUE);
        metaData.put(BaseConstants.OFFER_ID, String.valueOf(offer.getId()));
        metaData.put(BaseConstants.PLAN_ID, String.valueOf(plan.getId()));
        subscription.setPaymentMetaData(metaData);
    }

    private UserPlanDetails migrateForMusic(Subscription subscription) {
        final int planId = Integer.parseInt(subscription.getPaymentMetaData().get(PLAN_ID));
        final int offerId = Integer.parseInt(subscription.getPaymentMetaData().get(OFFER_ID));
        PlanType planType = PlanType.FREE;
        if (cachingService.containsPlan(planId)) {
            final Plan plan = cachingService.getPlan(planId);
            if (plan.getType() != PlanType.FREE) {
                planType = subscription.getAutoRenewalOff() ? PlanType.ONE_TIME_SUBSCRIPTION : PlanType.SUBSCRIPTION;
            }
        }
        UserPlanDetails userPlanDetails = UserPlanDetails.builder()
                .autoRenew(!subscription.getAutoRenewalOff())
                .endDate(subscription.getValidTillDate())
                .offerId(offerId)
                .paymentChannel(MIGRATION)
                .planId(planId)
                .paymentCode(subscription.getPaymentMethod())
                .planType(planType.getValue())
                .service(subscription.getService())
                .startDate(subscription.getSubscriptionDate())
                .uid(subscription.getUid())
                .meta(subscription.getPaymentMetaData())
                .unsubscribeOn(subscription.getAutoRenewalOff() ? subscription.getUnsubscriptionDate() : null)
                .planCount(1)
                .build();
        return userPlanDetails;
    }

    private UserPlanDetails migrate(String uid, String service, Integer offerId, Integer planId, Long count) {
        Date date = Date.from(Instant.now());
        UserPlanDetails userPlanDetails = UserPlanDetails.builder()
                .autoRenew(false)
                .endDate(date)
                .offerId(offerId)
                .paymentChannel(MIGRATION)
                .planId(planId)
                .service(service)
                .startDate(date)
                .uid(uid)
                .unsubscribeOn(date)
                .planCount(Math.toIntExact(count))
                .build();
        userdataService.addUserPlanDetails(userPlanDetails);
        return userPlanDetails;
    }

    private List<Subscription> provisionOffer(String uid, String service, Offer offer, Subscription subscription) {
        List<Subscription> newSubscriptions = new ArrayList<>();
        Map<String, String> products = offer.getProducts();
        for (String productId : products.keySet()) {
            Product product = cachingService.getProduct(productId);
            Subscription sub = Subscription.builder()
                    .paymentMethod(subscription.getPaymentMethod())
                    .paymentMetaData(subscription.getPaymentMetaData())
                    .partnerProductId(product.getId())
                    .packGroup(product.getPackGroup())
                    .nextChargingDate(subscription.getNextChargingDate())
                    .deactivationChannel(subscription.getDeactivationChannel())
                    .autoRenewalOff(subscription.getAutoRenewalOff())
                    .active(subscription.isActive())
                    .productId(product.getId())
                    .renewalUnderProcess(subscription.getRenewalUnderProcess())
                    .service(subscription.getService())
                    .subscriptionDate(subscription.getSubscriptionDate())
                    .subscriptionEndDate(subscription.getSubscriptionEndDate())
                    .subscriptionInProgress(subscription.getSubscriptionInProgress())
                    .subStatus(subscription.getSubStatus())
                    .uid(subscription.getUid())
                    .unsubscriptionDate(subscription.getUnsubscriptionDate())
                    .validTillDate(subscription.getValidTillDate())
                    .build();
            newSubscriptions.add(sub);
        }
        //insert updated old subscription into db
        if (WynkService.fromString(service) == WynkService.AIRTEL_TV)
            newSubscriptions.add(subscription);
        return newSubscriptions;
    }

    private List<Subscription> provisionFreeOffer(String uid, String service, Offer offer, Plan plan, Subscription subscription) {
        List<Subscription> newSubscriptions = new ArrayList<>();
        Map<String, String> products = offer.getProducts();
        for (String productId : products.keySet()) {
            Product product = cachingService.getProduct(productId);
            Date validTill = new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(plan.getPeriod().getValidity(), plan.getPeriod().getTimeUnit()));
            Subscription sub = Subscription.builder()
                    .paymentMethod(FREE)
                    .paymentMetaData(subscription.getPaymentMetaData())
                    .partnerProductId(product.getId())
                    .packGroup(product.getPackGroup())
                    .nextChargingDate(subscription.getNextChargingDate())
                    .deactivationChannel(subscription.getDeactivationChannel())
                    .autoRenewalOff(subscription.getAutoRenewalOff())
                    .active(subscription.isActive())
                    .productId(product.getId())
                    .renewalUnderProcess(subscription.getRenewalUnderProcess())
                    .service(service)
                    .subscriptionDate(subscription.getSubscriptionDate())
                    .subscriptionEndDate(subscription.getSubscriptionEndDate())
                    .subscriptionInProgress(subscription.getSubscriptionInProgress())
                    .subStatus(subscription.getSubStatus())
                    .uid(subscription.getUid())
                    .unsubscriptionDate(subscription.getUnsubscriptionDate())
                    .validTillDate(validTill)
                    .build();
            newSubscriptions.add(sub);
        }
        //insert updated old subscription into db
        if (WynkService.fromString(service) == WynkService.AIRTEL_TV)
            newSubscriptions.add(subscription);
        userdataService.addAllSubscriptions(uid, service, newSubscriptions);
        return newSubscriptions;
    }
}
