package in.wynk.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.constant.CommonLoggingMarker;
import in.wynk.exception.WynkRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;

@Slf4j
public class ChecksumUtils {

    public static <T> RequestEntity<T> buildEntityWithChecksum(String endpoint, String clientId, String clientSecret, T body, HttpMethod method) {
        String checksum = generate(clientId, clientSecret, endpoint, method, body, null);
        HttpHeaders headers = new HttpHeaders();
        headers.add(BaseConstants.PARTNER_X_CHECKSUM_TOKEN, checksum);
        return new RequestEntity<>(body, headers, method, URI.create(endpoint));
    }

    public static <T> RequestEntity<T> buildEntityWithAuthHeaders(String endpoint, String clientId, String clientSecret, T body, HttpMethod method) {
        Long timestamp = System.currentTimeMillis();
        String checksum = generate(clientId, clientSecret, endpoint, method, body, timestamp);
        HttpHeaders headers = new HttpHeaders();
        headers.add(BaseConstants.X_WYNK_ATNK, clientId + BaseConstants.COLON + checksum);
        headers.add(BaseConstants.X_WYNK_DATE, String.valueOf(timestamp));
        return new RequestEntity<>(body, headers, method, URI.create(endpoint));
    }

    public static <T> String generate(String clientId, String secret, String url, HttpMethod method, T body) {
        return generate(clientId, secret, url, method, body, null);
    }

    public static <T> String generate(String clientId, String secret, String url, HttpMethod method, T body, Long timestamp) {
        if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(secret)) {
            try {
                URI uri = new URI(url);
                StringBuilder builder = new StringBuilder(method.name()).append(uri.getPath());
                if (StringUtils.isNotEmpty(uri.getQuery()))
                    builder.append(BaseConstants.QUESTION_MARK).append(uri.getQuery());
                if (body != null)
                    builder.append(BeanLocatorFactory.getBean(ObjectMapper.class).writeValueAsString(body));
                if (timestamp != null)
                    builder.append(timestamp);
                return EncryptionUtils.calculateRFC2104HMAC(builder.toString(), secret);
            } catch (SignatureException | URISyntaxException | JsonProcessingException e) {
                log.error(CommonLoggingMarker.CHECKSUM_GENERATION_FAILURE, "Failed to generate checksum for clientId: {}", clientId, e);
                throw new WynkRuntimeException("Failed to generate checksum for clientId: {}" + clientId, e);
            }
        } else {
            throw new WynkRuntimeException("Client id or secret can't be empty or null");
        }
    }

}
