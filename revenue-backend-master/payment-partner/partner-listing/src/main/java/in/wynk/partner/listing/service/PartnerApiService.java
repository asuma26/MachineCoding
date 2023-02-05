package in.wynk.partner.listing.service;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.partner.listing.constant.BeanConstant;
import in.wynk.partner.listing.constant.PaymentPartnerConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static in.wynk.partner.listing.constant.ChannelPartnerLoggingMarker.ACTIVE_PLANS_API_ERROR;
import static in.wynk.partner.listing.constant.ChannelPartnerLoggingMarker.ELIGIBLE_PLANS_API_ERROR;

/**
 * @author Abhishek
 * @created 01/09/20
 */
@Slf4j
@Service
public class PartnerApiService {

    @Value("${service.subscription.api.endpoint.eligiblePlan}")
    private String partnerEligiblePlansEndpoint;

    @Value("${service.subscription.api.endpoint.activePlan}")
    private String activePlansEndpoint;

    private final RestTemplate restTemplate;
    private final WynkApplicationContext myApplicationContext;

    public PartnerApiService(@Qualifier(BeanConstant.SUBSCRIPTION_S2S_REST_TEMPLATE) RestTemplate restTemplate, WynkApplicationContext myApplicationContext) {
        this.restTemplate = restTemplate;
        this.myApplicationContext = myApplicationContext;
    }


    public PartnerEligiblePlansResponse getAllPlansForPartner(String partnerName, String service) {
        PartnerEligiblePlansResponse partnerEligiblePlansResponse = null;
        try {
            URI uri = new URIBuilder(partnerEligiblePlansEndpoint).addParameter(PaymentPartnerConstants.PARTNER_NAME, partnerName).addParameter(PaymentPartnerConstants.SERVICE, service).build();
            RequestEntity<Void> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(uri.toString(), myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
            ResponseEntity<PartnerEligiblePlansResponse> responseEntity = restTemplate.exchange(requestEntity, PartnerEligiblePlansResponse.class);
            partnerEligiblePlansResponse = responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            AnalyticService.update(PaymentPartnerConstants.RESPONSE_CODE, ex.getRawStatusCode());
            AnalyticService.update(PaymentPartnerConstants.ERROR_RESPONSE, ex.getResponseBodyAsString());
            log.error(ELIGIBLE_PLANS_API_ERROR, ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error(ELIGIBLE_PLANS_API_ERROR, ex.getMessage(), ex);
        }
        return partnerEligiblePlansResponse;
    }

    public UserActivePlansResponse getAllActivePlans(String uid, String service) {
        UserActivePlansResponse userActivePlansResponse = null;
        try {
            URI uri = new URIBuilder(activePlansEndpoint).addParameter(PaymentPartnerConstants.UID, uid).addParameter(PaymentPartnerConstants.SERVICE, service).build();
            RequestEntity<Void> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(uri.toString(), myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
            ResponseEntity<UserActivePlansResponse> responseEntity = restTemplate.exchange(requestEntity, UserActivePlansResponse.class);
            userActivePlansResponse = responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            AnalyticService.update(PaymentPartnerConstants.RESPONSE_CODE, ex.getRawStatusCode());
            AnalyticService.update(PaymentPartnerConstants.ERROR_RESPONSE, ex.getResponseBodyAsString());
            log.error(ACTIVE_PLANS_API_ERROR, ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error(ACTIVE_PLANS_API_ERROR, ex.getMessage(), ex);
        }
        return userActivePlansResponse;
    }
}
