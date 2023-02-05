package in.wynk.subscription.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.advice.TimeIt;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.subscription.common.dto.UserCachePurgeRequest;
import in.wynk.subscription.core.constants.BeanConstant;
import in.wynk.subscription.core.constants.SubscriptionLoggingMarkers;
import in.wynk.subscription.core.dto.ClientEventDetails;
import in.wynk.subscription.service.INonRevenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service(BeanConstant.UT_S2S_CLIENT)
public class NonRevenueUserTargetingService implements INonRevenueService {
    @Autowired
    private WynkApplicationContext applicationContext;

    @Value("${client.targeting.s2s.evict.endpoint}")
    private String utCacheEvictEndPoint;
    private final RestTemplate template;

    public NonRevenueUserTargetingService(ObjectMapper mapper, @Qualifier("internalServiceTemplate") RestTemplate template) {
        this.template = template;
    }

    @Override
    @TimeIt
    public void publishEvent(ClientEventDetails eventDetails) {
        try {
            UserCachePurgeRequest utCacheEvictDTO = UserCachePurgeRequest.builder().service(eventDetails.getService()).uid(eventDetails.getUid()).build();
            RequestEntity<UserCachePurgeRequest> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(utCacheEvictEndPoint, applicationContext.getClientId(), applicationContext.getClientSecret(), utCacheEvictDTO, HttpMethod.POST);
            template.exchange(requestEntity, Void.class);
        } catch (Exception e) {
            log.error(SubscriptionLoggingMarkers.USER_TARGETING_ERROR, "Unable to publish subscription event {} to user targeting client for uid {}, service{} due to {}", eventDetails.getUid(), eventDetails.getService(), e.getMessage(), e);
        }
    }

}
