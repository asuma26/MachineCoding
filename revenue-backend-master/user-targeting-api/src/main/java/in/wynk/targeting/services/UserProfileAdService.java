package in.wynk.targeting.services;

import in.wynk.cache.aspect.advice.CacheEvict;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.subscription.common.dto.UserProfileRequest;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import in.wynk.targeting.core.dao.entity.mongo.AdsOfferBlacklisted;
import in.wynk.targeting.core.dao.entity.mongo.AudioAdMeta;
import in.wynk.targeting.core.dao.entity.mongo.VideoAdMeta;
import in.wynk.targeting.core.dao.repository.mongo.AdsOfferBlacklistedMongoRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static in.wynk.cache.constant.BeanConstant.L2CACHE_MANAGER;
import static in.wynk.targeting.constant.UserTargetingConstants.USER_PROFILE_REST_TEMPLATE;
import static in.wynk.targeting.core.constant.UTLoggingMarkers.USER_PROFILE_ERROR;

@Slf4j
@Service
@NoArgsConstructor
public class UserProfileAdService {

    @Autowired
    private WynkApplicationContext myApplicationContext;

    @Autowired
    private AdsOfferBlacklistedMongoRepository adsOfferBlacklistedMongoRepository;

    @Autowired
    private AdConfigService adConfigService;

    @Value("${user.subscription.endpoint.userProfile}")
    private String userProfileEndPoint;

    @Autowired
    @Qualifier(USER_PROFILE_REST_TEMPLATE)
    private RestTemplate userProfileRestTemplate;

    public UserProfile getUserProfile(String uid, String service) {
        UserProfile userProfile = getUserProfileInternal(uid, service);
        if (userProfile.getLowestValidity()<= System.currentTimeMillis()) {
            evictUserProfileAdCache(uid, service);
            userProfile = getUserProfileInternal(uid, service);
        }
        return userProfile;
    }

    @Cacheable(cacheName = "UserProfileAdService", cacheKey = "'user-plan:'+ #uid + ':' + #service", l2CacheTtl = 2 * 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    private UserProfile getUserProfileInternal(String uid, String service) {
        UserProfile userProfile = null;
        UserProfileRequest userProfileRequest = UserProfileRequest.builder().uid(uid).service(service).build();
        try {
            RequestEntity<UserProfileRequest> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(userProfileEndPoint, myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), userProfileRequest, HttpMethod.POST);
            ResponseEntity<UserProfile> responseEntity = userProfileRestTemplate.exchange(requestEntity, UserProfile.class);
            userProfile = responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            log.error(USER_PROFILE_ERROR,ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error(USER_PROFILE_ERROR,ex.getMessage(), ex);
        }
        return userProfile;
    }

    public List<AdsOfferBlacklisted> getAdsOfferBlacklistedFromUserProfile(UserProfile userProfile) {

        List<AdsOfferBlacklisted> adsOfferBlacklistedList = new ArrayList<>();
        try {
            adsOfferBlacklistedList= getListOfBlacklistedCpsWithOffer(userProfile.getOfferId());
            return adsOfferBlacklistedList;
        } catch(Exception ex) {
            log.error(USER_PROFILE_ERROR,ex.getMessage(), ex);
        }
        return adsOfferBlacklistedList;
    }

    private List<AdsOfferBlacklisted> getListOfBlacklistedCpsWithOffer(List<Integer> offerIds) {
        List<Optional<AdsOfferBlacklisted>> optionalBlacklistedlist = offerIds.stream()
                .map(offerId->adsOfferBlacklistedMongoRepository.findById(offerId))
                .collect(Collectors.toList());
        List<AdsOfferBlacklisted> adsBlacklistedList = optionalBlacklistedlist.stream().
                filter(adsBlackListOption->adsBlackListOption.isPresent()).
                map(adsBlackListOption->adsBlackListOption.get()).
                collect(Collectors.toList());
        return adsBlacklistedList;
    }

    public Map<AdType,Set<String>> getAdTypeBlackListedCpMap(List<AdsOfferBlacklisted> listOfAddsOfferBlacklist) {
        Map<AdType,Set<String>> adTypeCpMap = new HashMap<>();
        for (AdsOfferBlacklisted adsOfferBlacklisted: listOfAddsOfferBlacklist) {
            AdType adType = adsOfferBlacklisted.getType();
            if(adTypeCpMap.containsKey(adType)) {
                Set<String> blacklistedCps = adTypeCpMap.get(adType)==null?new HashSet<>():adTypeCpMap.get(adType);
                blacklistedCps.addAll(adsOfferBlacklisted.getBlacklistedCps());
                adTypeCpMap.put(adType,blacklistedCps);
            } else {
                adTypeCpMap.put(adType, adsOfferBlacklisted.getBlacklistedCps().stream().collect(Collectors.toSet()));
            }
        }
        return adTypeCpMap;
    }

    public void updateListOfCpsInAdConfig(Map<AdType,Set<String>> adTypeCpMap, List<AdConfig> filteredAdConfigs){
        try {
            for (Map.Entry<AdType, Set<String>> adBlackListEntry : adTypeCpMap.entrySet()) {
                for (AdConfig adConfig : filteredAdConfigs) {
                    updateBlacklistedCps(adConfig, adBlackListEntry.getKey(), adBlackListEntry.getValue());
                }
            }
        }catch(Exception ex) {
            log.error(USER_PROFILE_ERROR,ex.getMessage(), ex);
        }
    }

    private void updateBlacklistedCps(AdConfig adConfig,AdType adType, Set<String> blacklistedCpsToBeAdded) {
           if(AdType.AUDIO_AD==adType && AdType.AUDIO_AD==adConfig.getType()) {
               AudioAdMeta adConfigMeta = adConfig.getMeta();
               if(adConfigMeta!=null) {
                   Set<String> existingBlacklistedCps = adConfigMeta.getBlacklistedCps() == null ? new HashSet<>() : adConfigMeta.getBlacklistedCps().stream().collect(Collectors.toSet());
                   existingBlacklistedCps.addAll(blacklistedCpsToBeAdded);
                   adConfigMeta.setBlacklistedCps(existingBlacklistedCps.stream().collect(Collectors.toList()));
                   adConfig.setMeta(adConfigMeta);
               }
           } else if(AdType.VIDEO_AD==adType && AdType.VIDEO_AD==adConfig.getType()) {
               VideoAdMeta adConfigMeta = adConfig.getMeta();
               if(adConfigMeta!=null) {
                   Set<String> existingBlacklistedCps = adConfigMeta.getBlacklistedCps() == null ? new HashSet<>() : adConfigMeta.getBlacklistedCps().stream().collect(Collectors.toSet());
                   existingBlacklistedCps.addAll(blacklistedCpsToBeAdded);
                   adConfigMeta.setBlacklistedCps(existingBlacklistedCps.stream().collect(Collectors.toList()));
                   adConfig.setMeta(adConfigMeta);
               }
           }
      }

    @CacheEvict(cacheName = "UserProfileAdService", cacheKey = "'user-plan:'+ #uid + ':' + #service", cacheManager = L2CACHE_MANAGER)
    public void evictUserProfileAdCache(String uid, String service){}

}