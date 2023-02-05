package in.wynk.order.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.client.aspect.advice.ClientAware;
import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.enums.TransactionStatus;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.aspect.advice.ManageOrder;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.dto.WynkPlanDetails;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.common.event.OrderFulfilmentMessageThresholdExceedEvent;
import in.wynk.order.common.event.OrderPlacementMessageThresholdExceedEvent;
import in.wynk.order.common.message.OrderPlacementMessage;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.ExecutionType;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.core.dao.entity.OrderErrorDetails;
import in.wynk.order.dto.request.OrderNotificationRequest;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.event.*;
import in.wynk.order.service.ICacheService;
import in.wynk.order.service.IOrderManager;
import in.wynk.queue.constant.QueueConstant;
import in.wynk.queue.dto.MessageThresholdExceedEvent;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.common.message.SubscriptionProvisioningMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static in.wynk.queue.constant.BeanConstant.MESSAGE_PAYLOAD;

@Slf4j
@Service
public class OrderEventListener {

    private final ObjectMapper mapper;
    private final IOrderManager orderManager;
    private final ISqsManagerService sqsManagerService;
    private final ICacheService<Order> orderCacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final ClientDetailsCachingService clientDetailsCachingService;
    private final ICacheService<OrderErrorDetails> orderErrorDetailsCacheService;

    public OrderEventListener(ObjectMapper mapper, IOrderManager orderManager, ISqsManagerService sqsManagerService, ICacheService<Order> orderCacheService, ApplicationEventPublisher eventPublisher, ClientDetailsCachingService clientDetailsCachingService, ICacheService<OrderErrorDetails> orderErrorDetailsCacheService) {
        this.mapper = mapper;
        this.orderManager = orderManager;
        this.eventPublisher = eventPublisher;
        this.sqsManagerService = sqsManagerService;
        this.orderCacheService = orderCacheService;
        this.clientDetailsCachingService = clientDetailsCachingService;
        this.orderErrorDetailsCacheService = orderErrorDetailsCacheService;
    }

    @EventListener(MessageThresholdExceedEvent.class)
    @AnalyseTransaction(name = QueueConstant.DEFAULT_SQS_MESSAGE_THRESHOLD_EXCEED_EVENT)
    public void onAnyOrderMessageThresholdExceedEvent(MessageThresholdExceedEvent event) throws JsonProcessingException {
        AnalyticService.update(event);
        AnalyticService.update(MESSAGE_PAYLOAD, mapper.writeValueAsString(event));
    }


    @ClientAware(clientId = "#event.partnerId")
    @EventListener(OrderPlacementMessageThresholdExceedEvent.class)
    @AnalyseTransaction(name = "orderPlacementMessageThresholdExceedEvent")
    public void onOrderPlacementMessageThresholdExceedEvent(OrderPlacementMessageThresholdExceedEvent event) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        OrderResponse response = orderManager.notifyOrder(clientDetails.getAlias(), OrderNotificationRequest.builder()
                .msisdn(event.getMsisdn())
                .orderId(event.getOrderId())
                .executionType(ExecutionType.ASYNC)
                .callbackUrl(event.getCallbackUrl())
                .orderDetails(WynkOrderDetails.builder()
                        .status(OrderStatus.FAILED)
                        .type(event.getPartnerOrder().getType())
                        .statusDetail(OrderStatusDetail.ORDER_FAIL400)
                        .build())
                .build());
        AnalyticService.update(response);
        WynkOrderDetails details = ((OrderNotificationResponse) response).getOrderDetails();
        eventPublisher.publishEvent(OrderErrorEvent.builder()
                .msisdn(response.getMsisdn())
                .orderId(response.getOrderId())
                .partnerAlias(clientDetails.getAlias())
                .statusDetail(details.getStatusDetail())
                .type(event.getPartnerOrder().getType())
                .planId(event.getPartnerOrder().getPlanId())
                .uid(MsisdnUtils.getUidFromMsisdn(event.getMsisdn()))
                .build());
    }

    @EventListener(OrderFulfilmentMessageThresholdExceedEvent.class)
    @AnalyseTransaction(name = "orderFulfilmentMessageThresholdExceedEvent")
    public void onOrderFulfilmentMessageThresholdExceedEvent(OrderFulfilmentMessageThresholdExceedEvent event) throws JsonProcessingException {
        OrderResponse response = orderManager.notifyOrder(event.getPartnerId(), OrderNotificationRequest.builder()
                .msisdn(event.getMsisdn())
                .orderId(event.getOrderId())
                .executionType(ExecutionType.ASYNC)
                .callbackUrl(event.getCallbackUrl())
                .planDetails(mapper.readValue(event.getPlanDetails(), WynkPlanDetails.class))
                .orderDetails(WynkOrderDetails.builder()
                        .status(OrderStatus.FAILED)
                        .type(event.getPartnerOrder().getType())
                        .statusDetail(OrderStatusDetail.ORDER_FAIL403)
                        .build())
                .build());
        AnalyticService.update(response);
    }

    @ManageOrder(orderId = "#event.orderId")
    @EventListener(OrderFulfilmentMessageThresholdExceedEvent.class)
    @AnalyseTransaction(name = "scheduleOrderDeferredMessageThresholdExceedEvent")
    public void onScheduleOrderDeferredMessageThresholdExceedEvent(ScheduleOrderDeferredMessageThresholdExceedEvent event) {
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD006));
        OrderResponse response = orderManager.notifyOrder(event.getPartnerId(), OrderNotificationRequest.builder()
                .msisdn(event.getMsisdn())
                .orderId(event.getOrderId())
                .executionType(ExecutionType.ASYNC)
                .callbackUrl(event.getCallbackUrl())
                .planDetails(WynkPlanDetails.builder().planId(order.getPlanId()).build())
                .orderDetails(WynkOrderDetails.builder()
                        .statusDetail(OrderStatusDetail.ORDER_FAIL403)
                        .status(OrderStatus.FAILED)
                        .type(order.getType())
                        .build())
                .build());
        AnalyticService.update(response);
    }

    @EventListener(OrderAcknowledgedEvent.class)
    @AnalyseTransaction(name = "orderAcknowledgedEvent")
    public void onOrderAcknowledged(OrderAcknowledgedEvent event) {
        AnalyticService.update(event);
        // TODO:
    }

    @EventListener(ProvisionalOrderAcknowledgedEvent.class)
    @AnalyseTransaction(name = "provisionalOrderAcknowledgedEvent")
    public void onProvisionalOrderAcknowledged(ProvisionalOrderAcknowledgedEvent event) {
        AnalyticService.update(event);
        ClientDetails clientDetails = (ClientDetails) clientDetailsCachingService.getClientByAlias(event.getPartnerAlias());
        sqsManagerService.publishSQSMessage(OrderPlacementMessage.builder()
                .partnerOrder(event.getPartnerOrder())
                .callbackUrl(event.getCallbackUrl())
                .partnerId(clientDetails.getId())
                .orderId(event.getOrderId())
                .msisdn(event.getMsisdn())
                .build());
    }

    @EventListener(OrderFulfilledEvent.class)
    @AnalyseTransaction(name = "fulfilledOrderEvent")
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        AnalyticService.update(event);
        // TODO:
    }

    @EventListener(ProvisionalOrderFulfilledEvent.class)
    @AnalyseTransaction(name = "provisionalOrderFulfilledEvent")
    public void onOrderFulfilled(ProvisionalOrderFulfilledEvent event) {
        AnalyticService.update(event);
        sqsManagerService.publishSQSMessage(SubscriptionProvisioningMessage.builder()
                .transactionStatus(TransactionStatus.SUCCESS)
                .paymentPartner(event.getPartnerAlias())
                .paymentCode(event.getPaymentCode())
                .callbackUrl(event.getCallbackUrl())
                .referenceId(event.getOrderId())
                .paymentEvent(event.getType())
                .planId(event.getPlanId())
                .msisdn(event.getMsisdn())
                .uid(event.getUid())
                .build());
    }

    @EventListener(OrderErrorEvent.class)
    @AnalyseTransaction(name = "failureOrderEvent")
    public void onOrderError(OrderErrorEvent event) {
        AnalyticService.update(event);
        Order order = orderCacheService.getByPrimaryId(event.getOrderId());
        if (order != null) {
            order.setStatus(OrderStatus.FAILED.name());
            order.setUpdatedOn(Calendar.getInstance());
            order.persisted();
            orderCacheService.save(order);
        }
        orderErrorDetailsCacheService.save(OrderErrorDetails.builder()
                .uid(event.getUid())
                .type(event.getType().name())
                .id(event.getOrderId())
                .planId(event.getPlanId())
                .statusCode(event.getStatusDetail().name())
                .createdOn(Calendar.getInstance())
                .partnerAlias(event.getPartnerAlias())
                .build());
    }

}
