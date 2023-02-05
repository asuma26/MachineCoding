package in.wynk.targeting.services;

import in.wynk.advice.TimeIt;
import in.wynk.targeting.core.dao.entity.mongo.persona.UserPersona;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static in.wynk.targeting.constant.UserTargetingConstants.PERSONA_REST_TEMPLATE;
import static in.wynk.targeting.core.constant.UTLoggingMarkers.PERSONA_ERROR;

/**
 * @author Abhishek
 * @created 18/05/20
 */
@Service
@NoArgsConstructor
@Slf4j
public class PersonaService {

    @Autowired
    @Qualifier(PERSONA_REST_TEMPLATE)
    private RestTemplate personaRestTemplate;
    @Value("${user.persona.endpoint}")
    private String USER_PERSONA_ENDPOINT;
    @Autowired
    private UTCachingService cachingService;

//    @Cacheable(cacheName = "USER_PERSONA", cacheKey = "T(java.lang.String).format('%s:%s', #root.methodName, #userId)", cacheManager = L2CACHE_MANAGER, l2CacheTtl = 16 * 60 * 60)
    @TimeIt
    public UserPersona getUserPersona(String userId) {
        if (cachingService.isTestUser(userId)) {
            return cachingService.getUserContext(userId).getUserPersona();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-service", "ATV");
        httpHeaders.add("Authorization", "atv");
        try {
            URI uri = new URI(String.format(USER_PERSONA_ENDPOINT, userId));
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<UserPersona> responseEntity = personaRestTemplate.exchange(uri, HttpMethod.GET, entity, UserPersona.class);
            return responseEntity.getBody();
        } catch (HttpStatusCodeException hex) {
            log.error(PERSONA_ERROR, hex.getResponseBodyAsString(), hex);
        } catch (Exception e) {
            log.error(PERSONA_ERROR, e.getMessage(), e);
        }
        return null;
    }
}
