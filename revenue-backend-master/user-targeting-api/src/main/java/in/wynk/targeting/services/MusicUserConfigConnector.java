package in.wynk.targeting.services;

import com.google.gson.Gson;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.http.constant.HttpConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.SignatureException;

import static in.wynk.targeting.constant.UserTargetingConstants.PERSONA_REST_TEMPLATE;

@Service
public class MusicUserConfigConnector {
    private static final Logger logger = LoggerFactory.getLogger(MusicUserConfigConnector.class.getCanonicalName());

    @Value("${music.user.config.appId}")
    private String appId;
    @Value("${music.user.config.appSecret}")
    private String appSecret;

    @Autowired
    @Qualifier(PERSONA_REST_TEMPLATE)
    private RestTemplate restTemplate;
    @Autowired
    private Gson gson;

    private static String generateHMACSignature(String httpVerb, String requestUri, long requestTimestamp, String secret) throws SignatureException {
        logger.info("Generate signature for method {}, url {}, time {}, secret {}", httpVerb, requestUri, requestTimestamp, secret);
        String signature;
        String digestString = new StringBuilder(httpVerb).append(requestUri).append(requestTimestamp).toString();
        signature = EncryptUtils.calculateRFC2104HMAC(digestString, secret);
        return signature;
    }

    public <T> T getWithAuth(String urlStr, HttpHeaders requestHeaders, Class<T> clazz) throws Exception {
        logger.info("Request to fetch user config for url {}", urlStr);
        long currentTime = System.currentTimeMillis();
        URI uri = new URI(urlStr);
        String signature = generateHMACSignature("GET", uri.getPath(), currentTime, appSecret);
        populateHeaders(requestHeaders, signature, currentTime);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        logger.info("User Config Request url : [{}]", uri.getPath());
        String responseStr = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class).getBody();
        T response = gson.fromJson(responseStr, clazz);
        logger.info("User Config Response : [{}]", responseStr);
        return response;
    }

    private void populateHeaders(HttpHeaders requestHeaders, String signature, long currentTime) {
        logger.info("Request to populate headers");
        requestHeaders.set(HttpConstant.X_BSY_ATKN, new StringBuilder(appId).append(":").append(signature).toString());
        requestHeaders.set(HttpConstant.X_BSY_DATE, String.valueOf(currentTime));
    }
}
