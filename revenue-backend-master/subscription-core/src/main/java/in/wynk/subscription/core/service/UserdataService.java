package in.wynk.subscription.core.service;

import com.github.annotation.analytic.core.service.AnalyticService;
import com.google.gson.Gson;
import in.wynk.cache.aspect.advice.CacheEvict;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.OfferDeviceMapping;
import in.wynk.subscription.core.dao.entity.OfferMsisdnMapping;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.dao.repository.userdata.OfferDeviceMappingRepository;
import in.wynk.subscription.core.dao.repository.userdata.OfferMsisdnMappingRepository;
import in.wynk.subscription.core.dao.repository.userdata.SubscriptionRepository;
import in.wynk.subscription.core.dao.repository.userdata.UserPlanDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static in.wynk.cache.constant.BeanConstant.L2CACHE_MANAGER;
import static in.wynk.common.constant.BaseConstants.MIGRATED;
import static in.wynk.common.constant.BaseConstants.TRUE;

@Service
@Slf4j
public class UserdataService implements IUserdataService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private OfferDeviceMappingRepository offerDeviceMappingRepository;

    @Autowired
    private MigrationService migrationService;

    @Autowired
    private SubscriptionCachingService cachingService;

    @Autowired
    private UserPlanDetailsRepository userPlanDetailsRepository;

    @Autowired
    private OfferMsisdnMappingRepository offerMsisdnMappingRepository;

    @Autowired
    private Gson gson;

    @Autowired
    private Executor executorService;

    private List<Subscription> getAllExistingSubscriptions(String uid, String service) {
        List<Subscription> existingSubscriptions = subscriptionRepository.findByUidAndService(uid, service);
        List<Subscription> migratedSubscriptions = migrationService.migrateSubscriptions(uid, service, existingSubscriptions);
        if (WynkService.fromString(service) == WynkService.MUSIC && migratedSubscriptions.size() > 0) {
            existingSubscriptions = existingSubscriptions.stream().filter(s -> migrationService.isEligibleForMigration(s)).collect(Collectors.toList());
        }
        existingSubscriptions.addAll(migratedSubscriptions);
        List<Subscription> finalActiveSubscriptions = existingSubscriptions.stream()
                .filter(subscription -> (subscription.isFree() && subscription.isSubscriptionActive()) || subscription.isPaidActiveOrInGrace())
                .collect(Collectors.toList());
        return finalActiveSubscriptions;
    }

    @Override
    public List<Subscription> getAllSubscriptions(String uid, String service) {
        List<Subscription> finalActiveSubscriptions = getAllExistingSubscriptions(uid, service);
        List<Subscription> subsToBeDeleted = finalActiveSubscriptions.stream().filter(s -> !cachingService.containsPackGroup(s.getPackGroup())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(subsToBeDeleted)) {
            Map<String, Integer> toBeDeleted = subsToBeDeleted.stream().collect(Collectors.toMap(Subscription::getPackGroup, Subscription::getPartnerProductId));
            AnalyticService.update("EXTRA_PACKS", gson.toJson(toBeDeleted));
            executorService.execute(() -> subscriptionRepository.deleteAll(subsToBeDeleted));
        }
        return finalActiveSubscriptions.stream().filter(s -> !StringUtils.equalsIgnoreCase(s.getPackGroup(), "wynk_atv_base") && cachingService.containsPackGroup(s.getPackGroup())).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheName = "UserdataService", cacheKey = "'subs:' + #service + ':' + #uid", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public void addAllSubscriptions(String uid, String service, Iterable<Subscription> subscriptions) {
        subscriptionRepository.saveAll(subscriptions);
    }

    @Override
    @CacheEvict(cacheName = "UserdataService", cacheKey = "'offer-device:'+ #service + ':' + #msisdn + ':' + #deviceId", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public void addAllOfferDeviceMapping(String msisdn, String service, String deviceId, Iterable<OfferDeviceMapping> offerDeviceMappings) {
        offerDeviceMappingRepository.saveAll(offerDeviceMappings);
    }

    @Override
    @Cacheable(cacheName = "UserdataService", cacheKey = "'offer-device:'+ #service + ':' + #msisdn + ':' + #deviceId", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public List<OfferDeviceMapping> getAllOfferDeviceMapping(String msisdn, String service, String deviceId) {
        log.debug("Fetching user device mapping");
        return offerDeviceMappingRepository.findByServiceAndDeviceId(service, deviceId);
    }

    @Override
    @CacheEvict(cacheName = "UserdataService", cacheKey = "'user-plan:'+ #userPlanDetails.getUid() + ':' + #userPlanDetails.getService()", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public void addUserPlanDetails(UserPlanDetails userPlanDetails) {
        userPlanDetailsRepository.save(userPlanDetails);
    }

    @Override
    @Cacheable(cacheName = "UserdataService", cacheKey = "'user-plan:'+ #uid + ':' + #service", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public List<UserPlanDetails> getAllUserPlanDetails(String uid, String service) {
        return userPlanDetailsRepository.findByServiceAndUid(service, uid);
    }

    @Override
    @Cacheable(cacheName = "UserdataService", cacheKey = "'user-plan:'+ #uid + ':' + #service", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public List<UserPlanDetails> getAllUserPlanDetails(String uid, String service, String msisdn) {
        List<UserPlanDetails> userPlanDetailsList = userPlanDetailsRepository.findByServiceAndUid(service, uid);
        List<UserPlanDetails> finalUserPlanDetails = userPlanDetailsList;
        // TODO:: users which are not migrated will get migrate now, remove once all users are migrated
        if (CollectionUtils.isEmpty(userPlanDetailsList)) {
            List<UserPlanDetails> migratedPlanDetails = migrationService.migrateFromSubscriptionToUserPlan(getAllSubscriptions(uid, service));
            addAllUserPlanDetails(uid, service, migratedPlanDetails);
            userPlanDetailsList.addAll(migratedPlanDetails);
            finalUserPlanDetails = migrationService.migrateUserPlanDetails(uid, service, msisdn, userPlanDetailsList);
        }
        List<UserPlanDetails> userPlanDetails605And606WithNotMigratedField = finalUserPlanDetails.stream()
                .filter(userPlanDetails -> Arrays.asList(606,6016,620,6067,60067).contains(userPlanDetails.getPlanId()) && (userPlanDetails.getMeta().isEmpty() || !userPlanDetails.getMeta().containsKey(MIGRATED)))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userPlanDetails605And606WithNotMigratedField)) {
            finalUserPlanDetails.removeAll(userPlanDetails605And606WithNotMigratedField);
            Optional<Subscription> wynkAtvBaseSubscription = getAllExistingSubscriptions(uid, service).stream().filter(subscription -> subscription.getPackGroup().equals("wynk_atv_base")).findAny();
            if (wynkAtvBaseSubscription.isPresent()) {
                userPlanDetails605And606WithNotMigratedField.forEach(userPlanDetails -> {
                    userPlanDetails.setEndDate(wynkAtvBaseSubscription.get().getValidTillDate());
                    userPlanDetails.getMeta().put(MIGRATED, TRUE);
                });
                addAllUserPlanDetails(uid, service, userPlanDetails605And606WithNotMigratedField);
            }
            finalUserPlanDetails.addAll(userPlanDetails605And606WithNotMigratedField);
        }
        return finalUserPlanDetails;
    }

    @Override
    @CacheEvict(cacheName = "UserdataService", cacheKey = "'user-plan:'+ #uid + ':' + #service", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public void addAllUserPlanDetails(String uid, String service, Iterable<UserPlanDetails> userPlanDetails) {
        userPlanDetailsRepository.saveAll(userPlanDetails);
    }

    public List<OfferMsisdnMapping> getAllOfferMsisdnMapping(String service, String msisdn) {
        return offerMsisdnMappingRepository.findByServiceAndMsisdn(service, msisdn);
    }

}