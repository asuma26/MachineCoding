package in.wynk.coupon.core.service.impl;

import in.wynk.common.constant.BaseConstants;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.dto.WynkResponse;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.coupon.core.service.IUserProfileService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.common.dto.ActivePlansResponse;
import in.wynk.subscription.common.dto.ThanksSegmentResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static in.wynk.coupon.core.constant.BeanConstant.SUBSCRIPTION_SERVICE_S2S_TEMPLATE;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Value("${service.subscription.api.endpoint.thanksSegmentEndpoint}")
    private String thanksSegmentEndpoint;
    @Value("${service.subscription.api.endpoint.activePlansEndpoint}")
    private String activePlansEndpoint;

    private final RestTemplate restTemplate;
    private final WynkApplicationContext myApplicationContext;

    public UserProfileServiceImpl(@Qualifier(SUBSCRIPTION_SERVICE_S2S_TEMPLATE) RestTemplate restTemplate, WynkApplicationContext myApplicationContext) {
        this.restTemplate = restTemplate;
        this.myApplicationContext = myApplicationContext;
    }


    @Override
    public List<Integer> fetchActivePlans(String uid, String service) {
        try {
            URI uri = new URIBuilder(activePlansEndpoint).addParameter(BaseConstants.UID, uid).addParameter(BaseConstants.SERVICE, service).build();
            RequestEntity<Void> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(uri.toString(), myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
            ResponseEntity<WynkResponse.WynkResponseWrapper<ActivePlansResponse>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WynkResponse.WynkResponseWrapper<ActivePlansResponse>>() {
            });
            if (Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getData())) {
                return response.getBody().getData().getPlanIds();
            }
        } catch (HttpStatusCodeException e) {
            throw new WynkRuntimeException(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new WynkRuntimeException("Unable to fetch active plans for uid " + uid + " and service " + service + " due to " + e.getMessage(), e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Map<String, List<String>> fetchThanksSegment(String msisdn, String service) {
        try {
            URI uri = new URIBuilder(thanksSegmentEndpoint).addParameter(BaseConstants.UID, msisdn).addParameter(BaseConstants.SERVICE, service).build();
            RequestEntity<Void> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(uri.toString(), myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
            ResponseEntity<WynkResponse.WynkResponseWrapper<ThanksSegmentResponse>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WynkResponse.WynkResponseWrapper<ThanksSegmentResponse>>() {
            });
            if (Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getData())) {
                return response.getBody().getData().getSegments();
            }
        } catch (HttpStatusCodeException e) {
            throw new WynkRuntimeException(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new WynkRuntimeException("Unable to fetch thanks segments for msisdn " + msisdn + " and service " + service + " due to " + e.getMessage(), e);
        }
        return MapUtils.EMPTY_MAP;
    }
}
