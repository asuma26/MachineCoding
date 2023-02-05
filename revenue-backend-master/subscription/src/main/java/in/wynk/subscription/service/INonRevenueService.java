package in.wynk.subscription.service;

import in.wynk.common.utils.EncryptionUtils;
import in.wynk.subscription.core.dto.ClientEventDetails;

import java.security.SignatureException;

public interface INonRevenueService {

    void publishEvent(ClientEventDetails clientEventDetails);

    default String generateHMACSignature(String httpVerb, String requestUri, String payload, long requestTimestamp, String secret) throws SignatureException {
        String digestString = new StringBuilder(httpVerb).append(requestUri).append(payload).append(requestTimestamp).toString();
        return EncryptionUtils.calculateRFC2104HMAC(digestString, secret);
    }

}
