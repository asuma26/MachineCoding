package in.wynk.order.service.impl;

import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.constant.BaseConstants;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.hystrix.advice.WynkHystrixCommand;
import in.wynk.order.common.dto.CancellationOrder;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.dto.WynkPlanDetails;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.dto.request.*;
import in.wynk.order.dto.response.OrderCancellationResponse;
import in.wynk.order.dto.response.OrderFulfilmentResponse;
import in.wynk.order.service.*;
import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.SinglePlanProvisionRequest;
import in.wynk.subscription.common.enums.ProvisionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class OrderFulfilmentServiceImpl implements IFulfilmentService {

    private final IDeferredService deferredService;
    private final ICacheService<Order> orderCacheService;
    private final ICancellationService cancellationService;
    private final ISubscriptionClientService subscriptionClientService;

    public OrderFulfilmentServiceImpl(IDeferredService deferredService, ICacheService<Order> orderCacheService, ICancellationService cancellationService, ISubscriptionClientService subscriptionClientService) {
        this.deferredService = deferredService;
        this.orderCacheService = orderCacheService;
        this.cancellationService = cancellationService;
        this.subscriptionClientService = subscriptionClientService;
    }

    @Override
    @WynkHystrixCommand(enabled = "#isAsyncSupported",
            fallbackMethod = "fallback",
            commandGroupKey = "${hystrix.order.commandGroupKey}",
            commandKey = "${hystrix.order.fulfilment.commandKey}",
            timeout = "${hystrix.order.fulfilment.timeout}",
            errorPercentage = "${hystrix.order.fulfilment.errorPercentage}")
    public OrderFulfilmentResponse fulfilSync(boolean isAsyncSupported, ClientDetails clientDetails, OrderFulfilmentRequest request) {
        log.info(OrderLoggingMarker.ORDER_FULFILMENT_INFO, "Order fulfillment request is received for payment partner: {} and order id: {}", clientDetails.getAlias(), request.getOrderId());
        try {
            Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
            WynkPlanDetails planDetails = provisionAndUpdateOrderSync(request, order);
            log.info(OrderLoggingMarker.ORDER_FULFILMENT_INFO, "Order is fulfilled for payment partner {} and order id {}", clientDetails.getAlias(), request.getOrderId());
            return OrderFulfilmentResponse.builder()
                    .orderId(request.getOrderId())
                    .msisdn(request.getMsisdn())
                    .planDetails(planDetails)
                    .orderDetails(WynkOrderDetails.builder()
                            .statusDetail(order.getStatus() == OrderStatus.DEFERRED ? OrderStatusDetail.ORDER_SUC205 : OrderStatusDetail.ORDER_SUC203)
                            .status(order.getStatus())
                            .type(order.getType())
                            .build())
                    .build();
        } catch (Exception e) {
            throw new WynkRuntimeException(e);
        }
    }

    @Override
    public OrderFulfilmentResponse fulfilAsync(ClientDetails clientDetails, OrderFulfilmentRequest request) {
        log.info(OrderLoggingMarker.ORDER_FULFILMENT_INFO, "Order fulfilment fallback request is received for partner: {} and order id: {} ", clientDetails.getAlias(), request.getOrderId());
        try {
            OrderFulfilmentFallbackRequest fallbackRequest = (OrderFulfilmentFallbackRequest) request;
            Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
            WynkPlanDetails planDetails = provisionAndUpdateOrderAsync(fallbackRequest, order);
            log.info(OrderLoggingMarker.ORDER_FULFILMENT_INFO, "Order is fulfilled for payment partner {} and order id {}", clientDetails.getAlias(), request.getOrderId());
            return OrderFulfilmentResponse.builder()
                    .orderId(request.getOrderId())
                    .msisdn(request.getMsisdn())
                    .orderDetails(WynkOrderDetails.builder()
                            .statusDetail(order.getStatus() == OrderStatus.DEFERRED ? OrderStatusDetail.ORDER_SUC205 : OrderStatusDetail.ORDER_SUC203)
                            .status(order.getStatus())
                            .type(order.getType())
                            .build())
                    .planDetails(planDetails)
                    .build();
        } catch (Exception e) {
            throw new WynkRuntimeException(e);
        }
    }

    public OrderFulfilmentResponse fallback(boolean ignore, ClientDetails clientDetails, OrderFulfilmentRequest request) {
        log.info(OrderLoggingMarker.ORDER_FULFILMENT_INFO, "executing order fulfilment fallback for partner: {} and order id: {}", clientDetails.getAlias(), request.getOrderId());
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
        return OrderFulfilmentResponse.builder()
                .orderId(request.getOrderId())
                .msisdn(request.getMsisdn())
                .orderDetails(WynkOrderDetails.builder()
                        .statusDetail(OrderStatusDetail.ORDER_SUC202)
                        .status(OrderStatus.ACKNOWLEDGED)
                        .type(order.getType())
                        .build())
                .build();
    }

    private WynkPlanDetails provisionAndUpdateOrderSync(OrderFulfilmentRequest request, Order order) {
        switch (order.getType()) {
            case PURCHASE:
            case SUBSCRIBE:
                try {
                    return handlePlanSubscribe(request, order);
                } catch (HttpStatusCodeException e) {
                    throw new WynkRuntimeException(OrderErrorType.ORD004, e);
                }
            case UNSUBSCRIBE:
                try {
                    return handlePlanUnSubscribe(request, order);
                } catch (HttpStatusCodeException e) {
                    throw new WynkRuntimeException(OrderErrorType.ORD012, e);
                }
            default:
                throw new WynkRuntimeException(OrderErrorType.ORD001);
        }
    }

    private WynkPlanDetails provisionAndUpdateOrderAsync(OrderFulfilmentFallbackRequest request, Order order) {
        switch (order.getType()) {
            case PURCHASE:
            case SUBSCRIBE:
                return handlePostPlanProvision(request.getPlanResponse(), order);
            case UNSUBSCRIBE:
                return handlePlanUnSubscribe(request, order);
            default:
                throw new WynkRuntimeException(OrderErrorType.ORD001);
        }
    }

    private WynkPlanDetails handlePlanSubscribe(OrderFulfilmentRequest request, Order order) {
        final PlanProvisioningRequest planProvisioningRequest = buildProvisioningRequest(request.getMsisdn(), order);
        final PlanProvisioningResponse provisionResponse = subscriptionClientService.subscribe(planProvisioningRequest);
        return handlePostPlanProvision(provisionResponse, order);
    }

    private WynkPlanDetails handlePlanUnSubscribe(OrderFulfilmentRequest request, Order order) {
        final OrderCancellationRequest cancellationRequest = buildOrderCancellationRequest(request, order);
        final OrderCancellationResponse cancellationResponse = cancellationService.cancel(order.getPartnerAlias(), cancellationRequest);
        return handlePlanUnsubscribed(cancellationResponse, order);
    }

    private WynkPlanDetails handlePostPlanProvision(PlanProvisioningResponse provisionResponse, Order order) {
        switch (Objects.requireNonNull(provisionResponse).getState()) {
            case SUBSCRIBED:
                ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
                Optional<Boolean> isAdditiveValiditySupported = clientDetails.getMeta(OrderConstant.ADDITIVE_VALIDITY_SUPPORT);
                if (provisionResponse.isAdditiveValidity() && !order.<Boolean>getMeta(OrderConstant.IS_PRE_FULFILLED).orElse(false) && isAdditiveValiditySupported.isPresent() && !isAdditiveValiditySupported.get()) {
                    handlePlanDeferred(provisionResponse, order);
                    break;
                }
                handlePlanSubscribed(order);
                break;
            case DEFERRED:
                handlePlanDeferred(provisionResponse, order);
                break;
            default:
                throw new WynkRuntimeException("unknown provision status");
        }
        return WynkPlanDetails.builder()
                .startDate(provisionResponse.getStartDate())
                .autoRenew(provisionResponse.isAutoRenew())
                .endDate(provisionResponse.getEndDate())
                .planId(provisionResponse.getPlanId())
                .build();
    }

    private void handlePlanSubscribed(Order order) {
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.FULFILLED.name());
        order.setUpdatedOn(Calendar.getInstance());
        orderCacheService.save(order);
        if (previousStatus == OrderStatus.DEFERRED) {
            deferredService.deferred(OrderDeferRequest.builder().orderId(order.getId()).orderStatus(OrderStatus.FULFILLED).build());
        }
    }

    private void handlePlanDeferred(PlanProvisioningResponse response, Order order) {
        Calendar validUntil = Calendar.getInstance();
        Calendar deferredUntil = Calendar.getInstance();
        validUntil.setTimeInMillis(response.getEndDate());
        deferredUntil.setTimeInMillis(response.getStartDate());
        OrderDeferRequest.OrderDeferRequestBuilder builder = OrderDeferRequest.builder();
        order.<String>getMeta(BaseConstants.CALLBACK_URL).ifPresent(builder::callbackUrl);
        deferredService.deferred(builder.orderId(order.getId()).untilDate(deferredUntil).validUntil(validUntil).preFulfilled(response.getState() == ProvisionState.SUBSCRIBED).build());
        order.setStatus(OrderStatus.DEFERRED.name());
        order.setUpdatedOn(Calendar.getInstance());
        orderCacheService.save(order);
    }

    private WynkPlanDetails handlePlanUnsubscribed(OrderCancellationResponse response, Order order) {
        order.setStatus(OrderStatus.FULFILLED.name());
        order.setUpdatedOn(Calendar.getInstance());
        orderCacheService.save(order);
        return response.getSubscriptionDetails();
    }

    private PlanProvisioningRequest buildProvisioningRequest(String msisdn, Order order) {
        return SinglePlanProvisionRequest.builder()
                .msisdn(msisdn)
                .uid(order.getUid())
                .planId(order.getPlanId())
                .referenceId(order.getId())
                .paymentPartner(order.getPartnerAlias())
                .paymentCode(order.getPaymentCode())
                .eventType(order.getType())
                .build();
    }

    private OrderCancellationRequest buildOrderCancellationRequest(OrderFulfilmentRequest request, Order order) {
        if (request instanceof OrderFulfilmentFallbackRequest) {
            return OrderCancellationFallbackRequest.builder()
                    .orderId(order.getId())
                    .planId(order.getPlanId())
                    .msisdn(request.getMsisdn())
                    .callbackUrl(request.getCallbackUrl())
                    .executionType(request.getExecutionType())
                    .reason(((CancellationOrder) request.getPartnerOrder()).getReason())
                    .planResponse(((OrderFulfilmentFallbackRequest) request).getPlanResponse())
                    .build();
        } else {
            return OrderCancellationRequest.builder()
                    .orderId(order.getId())
                    .planId(order.getPlanId())
                    .msisdn(request.getMsisdn())
                    .callbackUrl(request.getCallbackUrl())
                    .executionType(request.getExecutionType())
                    .reason(((CancellationOrder) request.getPartnerOrder()).getReason())
                    .build();
        }
    }

}
