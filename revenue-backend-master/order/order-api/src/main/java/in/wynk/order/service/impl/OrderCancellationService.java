package in.wynk.order.service.impl;

import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.dto.WynkPlanDetails;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.dto.request.OrderCancellationFallbackRequest;
import in.wynk.order.dto.request.OrderCancellationRequest;
import in.wynk.order.dto.response.OrderCancellationResponse;
import in.wynk.order.service.ICancellationService;
import in.wynk.order.service.ISubscriptionClientService;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Service
public class OrderCancellationService implements ICancellationService {

    private final ISubscriptionClientService subscriptionClientService;

    public OrderCancellationService(ISubscriptionClientService subscriptionClientService) {
        this.subscriptionClientService = subscriptionClientService;
    }

    @Override
    public OrderCancellationResponse cancelSync(String partnerId, OrderCancellationRequest request) {
        try {
            String uid = MsisdnUtils.getUidFromMsisdn(request.getMsisdn());
            PlanUnProvisioningRequest planUnProvisioningRequest = prepareUnProvisioningRequest(partnerId, uid, request);
            PlanProvisioningResponse unProvisionResponse = subscriptionClientService.unsubscribe(planUnProvisioningRequest);
            return OrderCancellationResponse.builder()
                    .orderId(request.getOrderId())
                    .subscriptionDetails(WynkPlanDetails.builder()
                            .planId(unProvisionResponse.getPlanId())
                            .endDate(unProvisionResponse.getEndDate())
                            .autoRenew(unProvisionResponse.isAutoRenew())
                            .build())
                    .build();
        } catch (HttpStatusCodeException e) {
            throw new WynkRuntimeException(OrderErrorType.ORD004, e);
        }
    }

    @Override
    public OrderCancellationResponse cancelAsync(String partnerId, OrderCancellationRequest request) {
        OrderCancellationFallbackRequest fallbackRequest = (OrderCancellationFallbackRequest) request;
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
        return OrderCancellationResponse.builder()
                .orderId(order.getId())
                .msisdn(request.getMsisdn())
                .subscriptionDetails(WynkPlanDetails.builder().planId(fallbackRequest.getPlanId()).endDate(fallbackRequest.getPlanResponse().getEndDate()).autoRenew(false).build())
                .build();
    }

    private PlanUnProvisioningRequest prepareUnProvisioningRequest(String partnerId, String uid, OrderCancellationRequest request) {
        return PlanUnProvisioningRequest.builder().uid(uid).planId(request.getPlanId()).referenceId(request.getOrderId()).paymentPartner(partnerId).build();
    }

}
