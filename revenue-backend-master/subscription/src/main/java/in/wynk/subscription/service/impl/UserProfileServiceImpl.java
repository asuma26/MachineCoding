package in.wynk.subscription.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.EncryptionUtils;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.subscription.common.dto.ActivePlansResponse;
import in.wynk.subscription.common.dto.ThanksSegmentResponse;
import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.core.service.UserdataService;
import in.wynk.subscription.core.service.UsermetaService;
import in.wynk.subscription.dto.response.UidToMsisdnResponse;
import in.wynk.subscription.service.IUserProfileService;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import in.wynk.vas.client.service.VasClientService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static in.wynk.cache.constant.BeanConstant.L2CACHE_MANAGER;
import static in.wynk.common.constant.BaseConstants.MSISDN;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Value("${client.music.s2s.appId}")
    private String musicAppId;
    @Value("${client.music.s2s.secret}")
    private String musicSecret;
    @Value("${service.music.api.endpoint.uidToMsisdn}")
    private String endpoint;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final VasClientService vasClientService;
    private final UsermetaService usermetaService;
    private final UserdataService userdataService;
    private final SubscriptionCachingService cachingService;

    public UserProfileServiceImpl(ObjectMapper objectMapper, @Qualifier("musicServiceTemplate") RestTemplate restTemplate, VasClientService vasClientService, UsermetaService usermetaService, UserdataService userdataService, SubscriptionCachingService cachingService) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.vasClientService = vasClientService;
        this.usermetaService = usermetaService;
        this.userdataService = userdataService;
        this.cachingService = cachingService;
    }

    @Override
    public ActivePlansResponse fetchActivePlans(String uid, WynkService service) {
        return ActivePlansResponse.builder()
                .planIds(userdataService.getAllUserPlanDetails(uid, service.getValue()).stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).map(UserPlanDetails::getPlanId).collect(Collectors.toList()))
                .build();
    }

    @Override
    public ThanksSegmentResponse fetchAllThanksSegments(String msisdn, WynkService service) {
        MsisdnOperatorDetails operatorDetails = vasClientService.allOperatorDetails(msisdn);
        Set<String> allSi = operatorDetails.getAllSI();
        Map<String, List<String>> segments = usermetaService.getAllThankSegments(msisdn, allSi)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toList())));
        return ThanksSegmentResponse.builder().segments(segments).build();
    }

    @Override
    public UserProfile fetchUserProfile(String uid, WynkService service) {
        List<UserPlanDetails> activeUserPlanDetails = userdataService.getAllUserPlanDetails(uid,service.getValue()).stream().filter(this::getActiveUserPlanDetails).collect(Collectors.toList());
        List<Integer> activePlanIds = activeUserPlanDetails.stream().map(userPlanDetail->userPlanDetail.getPlanId()).collect(Collectors.toList());
        List<Plan> plans = activePlanIds.stream().map(planId->cachingService.getPlan(planId)).collect(Collectors.toList());
        List<Integer> offerIds = plans.stream().map(plan->plan.getLinkedOfferId()).collect(Collectors.toList());
        long lowestValidity = activeUserPlanDetails.stream().mapToLong(userPlanDetails -> userPlanDetails.getEndDate().getTime()).min().orElse(0);
        UserProfile.UserProfileBuilder userProfileBuilder = UserProfile.builder().activePlanId(activePlanIds).offerId(offerIds).lowestValidity(lowestValidity);
        String msisdn = uidToMsisdn(uid, service);
        if (StringUtils.isNotBlank(msisdn)) {
            ThanksSegment thanksSegment = this.getHighestThanksSegmentByService(msisdn, service);
            if (thanksSegment != null) {
                Map<String, List<String>> map = new HashMap<>();
                List<String> list = new ArrayList<>();
                list.add(thanksSegment.getId());
                map.put(uid, list);
                userProfileBuilder.segments(map);
            }
        }
        return userProfileBuilder.build();
    }

    private boolean getActiveUserPlanDetails(UserPlanDetails userPlanDetail) {
        Period period = cachingService.getPlan(userPlanDetail.getPlanId()).getPeriod();
        return (userPlanDetail.isAutoRenew() ? period.getTimeUnit().toMillis(period.getGrace()) : 0) + userPlanDetail.getEndDate().getTime() > System.currentTimeMillis();
    }

    public ThanksSegment getHighestThanksSegmentByService(String msisdn, WynkService service) {
        Map<String, List<ThanksUserSegment>> allThanksUserSegments = usermetaService.getAllThankSegments(msisdn, vasClientService.allOperatorDetails(msisdn).getAllSI());
        Set<String> allThanksSegments = new HashSet<>();
        allThanksUserSegments.values().forEach(segments -> segments.stream().forEach(thanksUserSegment -> {
            if ((service==WynkService.AIRTEL_TV && thanksUserSegment.getService().equalsIgnoreCase(service.getValue())) || (service==WynkService.MUSIC && Arrays.asList("music", "hellotunes").contains(thanksUserSegment.getService()))) {
                allThanksSegments.add(thanksUserSegment.getServicePack());
            }
        }));
        return allThanksSegments.stream()
                .map(x -> cachingService.getThanksSegmentMap().get(x))
                .max(Comparator.comparing(ThanksSegment::getHierarchy))
                .orElse(ThanksSegment.builder().build());
    }

    @Cacheable(cacheName = "UID_TO_MSISDN", cacheKey = "#uid", l2CacheTtl = 7 * 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public String uidToMsisdn(String uid, WynkService wynkService) {
        String msisdn = null;
        try {
            if (wynkService == WynkService.MUSIC) {
                URI uri = UriComponentsBuilder.fromHttpUrl(endpoint).queryParam("uid", uid).build().toUri();
                long currentTime = System.currentTimeMillis();
                HttpHeaders requestHeaders = new HttpHeaders();
                String signature = EncryptionUtils.calculateRFC2104HMAC(new StringBuilder(HttpMethod.GET.name()).append(uri.getPath()).append("?uid="+uid).append(currentTime).toString(), musicSecret);
                requestHeaders.set(BaseConstants.X_BSY_ATKN, musicAppId + ":" + signature);
                requestHeaders.set(BaseConstants.X_BSY_DATE, String.valueOf(currentTime));
                requestHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                RequestEntity<String> requestEntity = new RequestEntity<>(null, requestHeaders, HttpMethod.GET, uri);
                AnalyticService.update("requestHeadersBody", requestEntity.toString());
                HttpEntity<String> response = restTemplate.exchange(requestEntity, String.class);
                UidToMsisdnResponse uidToMsisdnResponse = objectMapper.readValue(response.getBody(), UidToMsisdnResponse.class);
                if (uidToMsisdnResponse.isSuccess()) {
                    msisdn = uidToMsisdnResponse.getMsisdn();
                }
            } else if (wynkService == WynkService.AIRTEL_TV) {
                msisdn="";
            }
        } catch (Exception e) {
            AnalyticService.update(MSISDN, "can't find msisdn for given uid-service pair");
        }
        return MsisdnUtils.normalizePhoneNumber(msisdn);
    }

}