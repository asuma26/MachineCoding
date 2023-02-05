package in.wynk.order.service.impl;

import in.wynk.common.constant.BaseConstants;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.dto.WynkResponse;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.core.constant.BeanConstant;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.service.ISubscriptionClientService;
import in.wynk.subscription.common.dto.ActivePlanDetails;
import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
public class SubscriptionClientService implements ISubscriptionClientService {

    private final RestTemplate restTemplate;
    private final WynkApplicationContext myApplicationContext;
    @Value("${service.subscription.api.endpoint.activePlanDetails}")
    private String activePlanDetails;
    @Value("${service.subscription.api.endpoint.subscribePlan}")
    private String subscribePlanEndPoint;
    @Value("${service.subscription.api.endpoint.unSubscribePlan}")
    private String unsubscribePlanEndPoint;

    public SubscriptionClientService(@Qualifier(BeanConstant.SUBSCRIPTION_S2S_REST_TEMPLATE) RestTemplate restTemplate, WynkApplicationContext myApplicationContext) {
        this.restTemplate = restTemplate;
        this.myApplicationContext = myApplicationContext;
    }

    @Override
    public PlanProvisioningResponse subscribe(PlanProvisioningRequest request) {
        log.info("subscribing plan for uid: {}, reference Id {} and partner {} ", request.getUid(), request.getReferenceId(), request.getPaymentPartner());
        RequestEntity<PlanProvisioningRequest> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(subscribePlanEndPoint, myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), request, HttpMethod.POST);
        ResponseEntity<WynkResponse.WynkResponseWrapper<PlanProvisioningResponse>> provisionResponse = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WynkResponse.WynkResponseWrapper<PlanProvisioningResponse>>() {
        });
        return Objects.requireNonNull(provisionResponse.getBody()).getData();
    }

    @Override
    public PlanProvisioningResponse unsubscribe(PlanUnProvisioningRequest request) {
        log.info("unsubscribing plan for uid: {}, reference Id {} and partner {} ", request.getUid(), request.getReferenceId(), request.getPaymentPartner());
        RequestEntity<PlanUnProvisioningRequest> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(unsubscribePlanEndPoint, myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), request, HttpMethod.POST);
        ResponseEntity<WynkResponse.WynkResponseWrapper<PlanProvisioningResponse>> provisionResponse = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WynkResponse.WynkResponseWrapper<PlanProvisioningResponse>>() {
        });
        return Objects.requireNonNull(provisionResponse.getBody().getData());
    }

    @Override
    public ActivePlanDetails getActivePlan(String uid, int planId) {
        log.info("fetch active plan for uid: {}, planId {} ", uid, planId);
        try {
            URIBuilder builder = new URIBuilder(activePlanDetails);
            builder.addParameter(BaseConstants.UID, uid);
            builder.addParameter(BaseConstants.PLAN_ID, String.valueOf(planId));
            RequestEntity<PlanProvisioningRequest> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(builder.toString(), myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
            ResponseEntity<WynkResponse.WynkResponseWrapper<ActivePlanDetails>> activePlanResponse = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WynkResponse.WynkResponseWrapper<ActivePlanDetails>>() {
            });
            return Objects.requireNonNull(activePlanResponse.getBody().getData());
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD014, e);
        }
    }

}
