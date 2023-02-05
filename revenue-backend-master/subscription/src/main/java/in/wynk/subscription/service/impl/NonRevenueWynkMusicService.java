package in.wynk.subscription.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.advice.TimeIt;
import in.wynk.common.constant.BaseConstants;
import in.wynk.subscription.core.constants.BeanConstant;
import in.wynk.subscription.core.constants.SubscriptionLoggingMarkers;
import in.wynk.subscription.core.dto.ClientEventDetails;
import in.wynk.subscription.service.INonRevenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service(BeanConstant.WYNK_MUSIC_S2S_CLIENT)
public class NonRevenueWynkMusicService implements INonRevenueService {

    @Value("${client.music.s2s.appId}")
    private String musicAppId;
    @Value("${client.music.s2s.secret}")
    private String musicSecret;
    @Value("${client.music.s2s.endpoint}")
    private String musicEventEndpoint;
    private final ObjectMapper mapper;
    private final RestTemplate template;

    public NonRevenueWynkMusicService(ObjectMapper mapper, @Qualifier("internalServiceTemplate") RestTemplate template) {
        this.mapper = mapper;
        this.template = template;
    }

    @Override
    @TimeIt
    public void publishEvent(ClientEventDetails eventDetails) {
        try {
            URI uri = new URI(musicEventEndpoint);
            long currentTime = System.currentTimeMillis();
            HttpHeaders requestHeaders = new HttpHeaders();
            String payload = mapper.writeValueAsString(eventDetails);
            String signature = generateHMACSignature(HttpMethod.POST.name(), uri.getPath(), payload, currentTime, musicSecret);
            requestHeaders.set(BaseConstants.X_BSY_ATKN, musicAppId + ":" + signature);
            requestHeaders.set(BaseConstants.X_BSY_DATE, String.valueOf(currentTime));
            requestHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            RequestEntity<String> requestEntity = new RequestEntity<>(payload, requestHeaders, HttpMethod.POST, uri);
            AnalyticService.update("requestHeadersBody", requestEntity.toString());
            template.exchange(requestEntity, String.class);
        } catch (Exception e) {
            log.error(SubscriptionLoggingMarkers.WYNK_MUSIC_ERROR, "Unable to publish subscription event {} to music client for uid {}, planId {} due to {}", eventDetails.getEvent(), eventDetails.getUid(), e.getMessage(), e);
        }
    }
}
