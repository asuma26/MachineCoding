package in.wynk.order.service.impl;

import in.wynk.client.aspect.advice.ClientAware;
import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.aspect.advice.ManageOrder;
import in.wynk.order.common.dto.*;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.ExecutionType;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.core.utils.OrderIdentityGenerator;
import in.wynk.order.dto.request.*;
import in.wynk.order.dto.response.OrderFulfilmentResponse;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.dto.response.OrderPlacementResponse;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.event.*;
import in.wynk.order.service.*;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.enums.ProvisionState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Slf4j
@Service
public class OrderManagerImpl implements IOrderManager {

    private final IStatusService statusService;
    private final IFulfilmentService fulfilmentService;
    private final INotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final IAcknowledgementService acknowledgementService;

    public OrderManagerImpl(IStatusService statusService, IFulfilmentService fulfilmentService, INotificationService notificationService, ApplicationEventPublisher eventPublisher, IAcknowledgementService acknowledgementService) {
        this.statusService = statusService;
        this.eventPublisher = eventPublisher;
        this.fulfilmentService = fulfilmentService;
        this.notificationService = notificationService;
        this.acknowledgementService = acknowledgementService;
    }

    @Override
    @ClientAware(clientId = "#partnerId")
    @ManageOrder(partnerOrderId = "#request.partnerOrder.id")
    public OrderResponse placeOrder(String partnerId, OrderPlacementRequest request) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        try {
            OrderPlacementResponse ackResponse = acknowledgementService.place(request);
            if (EnumSet.of(OrderStatusDetail.ORDER_SUC201).contains(ackResponse.getOrderDetails().getStatusDetail())) {
                this.publishOrderAcknowledgedEvent(request, ackResponse);
                if (request.getExecutionType() == ExecutionType.SYNC) {
                    return fulfilOrder(clientDetails.getAlias(), OrderFulfilmentRequest.builder()
                            .msisdn(request.getMsisdn())
                            .orderId(ackResponse.getOrderId())
                            .callbackUrl(request.getCallbackUrl())
                            .partnerOrder(request.getPartnerOrder())
                            .executionType(request.getExecutionType())
                            .build());
                } else {
                    this.publishProvisionalOrderFulfilmentEvent(request, ackResponse);
                }
            } else if (EnumSet.of(OrderStatusDetail.ORDER_SUC200).contains(ackResponse.getOrderDetails().getStatusDetail())) {
                this.publishProvisionOrderAcknowledgedEvent(request, ackResponse);
            }

            return notifyOrder(clientDetails.getAlias(), OrderNotificationRequest.builder()
                    .orderDetails(ackResponse.getOrderDetails())
                    .executionType(request.getExecutionType())
                    .callbackUrl(request.getCallbackUrl())
                    .orderId(ackResponse.getOrderId())
                    .msisdn(request.getMsisdn())
                    .build());

        } catch (WynkRuntimeException e) {
            log.error(e.getMarker(), e.getMessage(), e);
            if (isPartnerAsyncNotSupported()) {
                String dummyOrderId = OrderIdentityGenerator.generate();
                this.publishOrderErrorEvent(request.getPartnerOrder().getPlanId(), request.getMsisdn(), dummyOrderId, request.getPartnerOrder().getType(), OrderStatusDetail.ORDER_FAIL401);
                return notifyOrder(clientDetails.getAlias(), OrderNotificationRequest.builder()
                        .orderId(dummyOrderId)
                        .msisdn(request.getMsisdn())
                        .callbackUrl(request.getCallbackUrl())
                        .executionType(request.getExecutionType())
                        .orderDetails(WynkOrderDetails.builder()
                                .statusDetail(OrderStatusDetail.ORDER_FAIL400)
                                .type(request.getPartnerOrder().getType())
                                .status(OrderStatus.FAILED)
                                .build())
                        .planDetails(WynkPlanDetails.builder()
                                .planId(request.getPartnerOrder().getPlanId())
                                .build())
                        .build());
            }
            throw e;
        }
    }

    @Override
    @ClientAware(clientAlias = "#clientAlias")
    @ManageOrder(orderId = "#request.orderId")
    public OrderResponse fulfilOrder(String clientAlias, OrderFulfilmentRequest request) {
        try {
            OrderFulfilmentResponse fulfilResponse = fulfilmentService.fulfil(request);
            if (EnumSet.of(OrderStatusDetail.ORDER_SUC203, OrderStatusDetail.ORDER_SUC205).contains(fulfilResponse.getOrderDetails().getStatusDetail())) {
                this.publishOrderFulfilledEvent(request, fulfilResponse);
                return notifyOrder(clientAlias, OrderNotificationRequest.builder()
                        .orderDetails(fulfilResponse.getOrderDetails())
                        .planDetails(fulfilResponse.getPlanDetails())
                        .executionType(request.getExecutionType())
                        .callbackUrl(request.getCallbackUrl())
                        .orderId(fulfilResponse.getOrderId())
                        .msisdn(request.getMsisdn())
                        .build());
            }

            this.publishProvisionalOrderFulfilmentEvent(request, fulfilResponse);
            return notifyOrder(clientAlias, OrderNotificationRequest.builder()
                    .orderDetails(fulfilResponse.getOrderDetails())
                    .planDetails(fulfilResponse.getPlanDetails())
                    .executionType(request.getExecutionType())
                    .callbackUrl(request.getCallbackUrl())
                    .orderId(fulfilResponse.getOrderId())
                    .msisdn(request.getMsisdn())
                    .build());

        } catch (WynkRuntimeException e) {
            log.error(e.getMarker(), e.getMessage(), e);
            if (isPartnerAsyncNotSupported()) {
                this.publishOrderErrorEvent(request.getPartnerOrder().getPlanId(), request.getMsisdn(), request.getOrderId(), request.getPartnerOrder().getType(), OrderStatusDetail.ORDER_FAIL401);
                return notifyOrder(clientAlias, OrderNotificationRequest.builder()
                        .planDetails(WynkPlanDetails.builder().planId(request.getPartnerOrder().getPlanId()).build())
                        .orderDetails(WynkOrderDetails.builder()
                                .statusDetail(OrderStatusDetail.ORDER_FAIL401)
                                .type(request.getPartnerOrder().getType())
                                .status(OrderStatus.FAILED)
                                .build())
                        .executionType(request.getExecutionType())
                        .callbackUrl(request.getCallbackUrl())
                        .orderId(request.getOrderId())
                        .msisdn(request.getMsisdn())
                        .build());
            }
            throw e;
        }
    }

    @Override
    @ClientAware(clientAlias = "#clientAlias")
    @ManageOrder(orderId = "#request.orderId")
    public OrderResponse notifyOrder(String clientAlias, OrderNotificationRequest request) {
        try {
            OrderNotificationResponse response = notificationService.notify(request);
            this.publishNotificationEvent(request, response);
            return response;
        } catch (WynkRuntimeException e) {
            log.error(e.getMarker(), e.getMessage(), e);
            if (isPartnerAsyncNotSupported() || request.getExecutionType() == ExecutionType.ASYNC) {
                this.publishOrderErrorEvent(request.getPlanDetails().getPlanId(), request.getMsisdn(), request.getOrderId(), request.getOrderDetails().getType(), OrderStatusDetail.ORDER_FAIL402);
                return OrderNotificationResponse.builder()
                        .orderDetails(WynkOrderDetails.builder()
                                .type(request.getOrderDetails().getType())
                                .statusDetail(OrderStatusDetail.ORDER_FAIL402)
                                .status(OrderStatus.FAILED)
                                .build())
                        .planDetails(request.getPlanDetails())
                        .orderId(request.getOrderId())
                        .msisdn(request.getMsisdn())
                        .build();
            }
            throw e;
        }
    }

    @Override
    @ManageOrder(orderId = "#request.orderId")
    public OrderResponse fulfilDeferredOrder(OrderDeferredFulfilRequest request) {
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD006));
        OrderFulfilmentFallbackRequest.OrderFulfilmentFallbackRequestBuilder<?, ?> requestBuilder = OrderFulfilmentFallbackRequest.builder().preFulfilled(request.isPreFulfilled()).executionType(ExecutionType.ASYNC).orderId(order.getId()).callbackUrl(request.getCallbackUrl());
        switch (order.getType()) {
            case PURCHASE:
                requestBuilder.partnerOrder(FreshOrder.builder()
                        .planId(order.getPlanId())
                        .orderDetails(PartnerOrderDetails.builder()
                                .currency(order.getCurrency())
                                .amount(order.getAmount())
                                .build())
                        .paymentDetails(PartnerPaymentDetails.builder()
                                .gatewayName(order.getPaymentCode())
                                .timestamp(order.getCreatedOn().getTimeInMillis())
                                .build())
                        .build());
                break;
            case SUBSCRIBE:
                requestBuilder.partnerOrder(RenewOrder.builder()
                        .planId(order.getPlanId())
                        .orderDetails(PartnerOrderDetails.builder()
                                .currency(order.getCurrency())
                                .amount(order.getAmount())
                                .build())
                        .paymentDetails(PartnerPaymentDetails.builder()
                                .gatewayName(order.getPaymentCode())
                                .timestamp(order.getCreatedOn().getTimeInMillis())
                                .build())
                        .build());
                break;
        }
        OrderResponse response = OrderFulfilmentResponse.builder()
                .orderId(order.getId())
                .orderDetails(WynkOrderDetails.builder()
                        .type(order.getType())
                        .status(OrderStatus.DEFERRED)
                        .statusDetail(OrderStatusDetail.ORDER_SUC207)
                        .build())
                .build();

        if (request.isPreFulfilled()) {
            requestBuilder.planResponse(PlanProvisioningResponse.builder()
                    .startDate(request.getDeferredUntil())
                    .state(ProvisionState.SUBSCRIBED)
                    .endDate(request.getValidUntil())
                    .planId(order.getPlanId())
                    .build());
            response = fulfilOrder(order.getPartnerAlias(), requestBuilder.build());
        } else {
            this.publishProvisionalOrderFulfilmentEvent(order.getPartnerAlias(), requestBuilder.build(), response, order.getUid());
        }
        return response;
    }

    @Override
    public OrderResponse orderStatus(String orderId, String msisdn) {
        return statusService.status(orderId, msisdn);
    }

    private boolean isPartnerAsyncNotSupported() {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        return !clientDetails.<Boolean>getMeta(BaseConstants.CLIENT_ASYNC_SUPPORTED).orElse(false);
    }

    private void publishOrderAcknowledgedEvent(OrderPlacementRequest request, OrderPlacementResponse response) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        eventPublisher.publishEvent(OrderAcknowledgedEvent.builder()
                .planId(request.getPartnerOrder().getPlanId())
                .partnerOrder(request.getPartnerOrder())
                .partnerAlias(clientDetails.getAlias())
                .orderId(response.getOrderId())
                .msisdn(response.getMsisdn())
                .build());
    }

    private void publishOrderFulfilledEvent(OrderFulfilmentRequest request, OrderFulfilmentResponse response) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        eventPublisher.publishEvent(OrderFulfilledEvent.builder()
                .uid(MsisdnUtils.getUidFromMsisdn(request.getMsisdn()))
                .status(response.getOrderDetails().getStatus())
                .planId(response.getPlanDetails().getPlanId())
                .type(response.getOrderDetails().getType())
                .partnerAlias(clientDetails.getAlias())
                .orderId(response.getOrderId())
                .msisdn(response.getMsisdn())
                .build());
    }

    private void publishProvisionOrderAcknowledgedEvent(OrderPlacementRequest request, OrderPlacementResponse response) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        eventPublisher.publishEvent(ProvisionalOrderAcknowledgedEvent.builder()
                .uid(MsisdnUtils.getUidFromMsisdn(response.getMsisdn()))
                .planId(request.getPartnerOrder().getPlanId())
                .type(response.getOrderDetails().getType())
                .partnerOrder(request.getPartnerOrder())
                .partnerAlias(clientDetails.getAlias())
                .callbackUrl(request.getCallbackUrl())
                .orderId(response.getOrderId())
                .msisdn(response.getMsisdn())
                .build());
    }

    @ClientAware(clientAlias = "#clientAlias")
    private void publishProvisionalOrderFulfilmentEvent(String clientAlias, OrderRequest request, OrderResponse orderResponse, String... args) {
        this.publishProvisionalOrderFulfilmentEvent(request, orderResponse, args);
    }

    private void publishProvisionalOrderFulfilmentEvent(OrderRequest request, OrderResponse orderResponse, String... args) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        ProvisionalOrderFulfilledEvent.ProvisionalOrderFulfilledEventBuilder<?, ?> builder = ProvisionalOrderFulfilledEvent.builder()
                .uid((args != null && args.length > 0 && StringUtils.isNotEmpty(args[0])) ? args[0] : MsisdnUtils.getUidFromMsisdn(request.getMsisdn()))
                .partnerAlias(clientDetails.getAlias())
                .callbackUrl(request.getCallbackUrl())
                .orderId(orderResponse.getOrderId())
                .msisdn(request.getMsisdn());
        if (request instanceof OrderPlacementFallbackRequest) {
            OrderPlacementFallbackRequest fallbackRequest = (OrderPlacementFallbackRequest) request;
            OrderPlacementResponse placementResponse = (OrderPlacementResponse) orderResponse;
            builder.status(placementResponse.getOrderDetails().getStatus())
                    .planId(fallbackRequest.getPartnerOrder().getPlanId())
                    .type(placementResponse.getOrderDetails().getType())
                    .paymentCode(getPaymentCode(fallbackRequest.getPartnerOrder().getType(), fallbackRequest.getPartnerOrder()));
        } else {
            OrderFulfilmentRequest fallbackRequest = (OrderFulfilmentRequest) request;
            OrderFulfilmentResponse fulfilmentResponse = (OrderFulfilmentResponse) orderResponse;
            builder.status(fulfilmentResponse.getOrderDetails().getStatus())
                    .planId(fallbackRequest.getPartnerOrder().getPlanId())
                    .type(fulfilmentResponse.getOrderDetails().getType())
                    .paymentCode(getPaymentCode(fallbackRequest.getPartnerOrder().getType(), fallbackRequest.getPartnerOrder()));
        }
        eventPublisher.publishEvent(builder.build());
    }

    private String getPaymentCode(PaymentEvent eventType, PartnerOrder partnerOrder) {
        if (EnumSet.of(PaymentEvent.PURCHASE, PaymentEvent.SUBSCRIBE).contains(eventType)) {
            return ((FreshOrder) partnerOrder).getPaymentDetails().getGatewayName();
        } else if (EnumSet.of(PaymentEvent.RENEW).contains(eventType)) {
            return ((RenewOrder) partnerOrder).getPaymentDetails().getGatewayName();
        } else {
            return null;
        }
    }

    private void publishNotificationEvent(OrderNotificationRequest request, OrderNotificationResponse response) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        eventPublisher.publishEvent(OrderNotifiedEvent.builder()
                .uid(MsisdnUtils.getUidFromMsisdn(request.getMsisdn()))
                .planId(response.getPlanDetails().getPlanId())
                .partnerAlias(clientDetails.getAlias())
                .orderId(request.getOrderId())
                .msisdn(request.getMsisdn())
                .build());
    }

    private void publishOrderErrorEvent(int planId, String msisdn, String orderId, PaymentEvent type, OrderStatusDetail statusDetail) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        eventPublisher.publishEvent(OrderErrorEvent.builder()
                .uid(MsisdnUtils.getUidFromMsisdn(msisdn))
                .partnerAlias(clientDetails.getAlias())
                .statusDetail(statusDetail)
                .orderId(orderId)
                .msisdn(msisdn)
                .planId(planId)
                .type(type)
                .build());
    }

}
