package in.wynk.order.service.impl;

import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.hystrix.advice.WynkHystrixCommand;
import in.wynk.order.common.dto.FreshOrder;
import in.wynk.order.common.dto.RenewOrder;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.core.dao.entity.Order.OrderBuilder;
import in.wynk.order.core.utils.OrderIdentityGenerator;
import in.wynk.order.dto.request.OrderPlacementFallbackRequest;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderPlacementResponse;
import in.wynk.order.service.IAcknowledgementService;
import in.wynk.order.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Slf4j
@Service
public class OrderAcknowledgementServiceImpl implements IAcknowledgementService {

    private final ICacheService<Order> orderCacheService;

    public OrderAcknowledgementServiceImpl(ICacheService<Order> orderCacheService) {
        this.orderCacheService = orderCacheService;
    }

    @Override
    @WynkHystrixCommand(enabled = "#isPartnerAsyncSupported",
            fallbackMethod = "fallback",
            commandGroupKey = "${hystrix.order.commandGroupKey}",
            commandKey = "${hystrix.order.placement.commandKey}",
            timeout = "${hystrix.order.placement.timeout}",
            errorPercentage = "${hystrix.order.placement.errorPercentage}")
    public OrderPlacementResponse placeSync(boolean isPartnerAsyncSupported, ClientDetails clientDetails, OrderPlacementRequest request) {
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_INFO, "order placement request is received for partner: {} and msisdn: {}", clientDetails.getAlias(), request.getMsisdn());
        try {
            return processOrder(request, clientDetails);
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD002, e);
        }
    }

    @Override
    public OrderPlacementResponse placeAsync(ClientDetails clientDetails, OrderPlacementRequest request) {
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_INFO, "async order placement request is received for partner: {} and msisdn: {}", clientDetails.getAlias(), request.getMsisdn());
        try {
            return processOrder(request, clientDetails);
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD002, e);
        }
    }

    public OrderPlacementResponse fallback(boolean ignore, ClientDetails clientDetails, OrderPlacementRequest request) {
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_INFO, "executing order placement fallback for partner: {} and msisdn: {}", clientDetails.getAlias(), request.getMsisdn());
        String fallbackOrderId = OrderIdentityGenerator.generate();
        return OrderPlacementResponse.builder()
                .orderId(fallbackOrderId)
                .msisdn(request.getMsisdn())
                .orderDetails(WynkOrderDetails.builder()
                        .statusDetail(OrderStatusDetail.ORDER_SUC200)
                        .status(OrderStatus.ACKNOWLEDGED)
                        .type(request.getPartnerOrder().getType())
                        .build())
                .build();
    }

    private OrderPlacementResponse processOrder(OrderPlacementRequest request, ClientDetails clientDetails) {
        if (OrderContext.getOrder().isPresent()) {
            Order existingOrder = OrderContext.getOrder().get();
            OrderStatusDetail orderStatusDetail = OrderStatusDetail.ORDER_SUC204;
            switch (existingOrder.getStatus()) {
                case DEFERRED:
                    orderStatusDetail = OrderStatusDetail.ORDER_SUC205;
                    break;
                case FULFILLED:
                    orderStatusDetail = OrderStatusDetail.ORDER_SUC203;
                    break;
                case FAILED:
                    orderStatusDetail = OrderStatusDetail.ORDER_FAIL403;
                    break;
            }
            log.info(OrderLoggingMarker.ORDER_PLACEMENT_INFO, "Order {} is already exist for client {} with partner order id {} and uid {}", existingOrder.getId(), existingOrder.getPartnerAlias(), request.getPartnerOrder().getId(), MsisdnUtils.getUidFromMsisdn(request.getMsisdn()));
            return OrderPlacementResponse.builder()
                    .orderId(existingOrder.getId())
                    .msisdn(request.getMsisdn())
                    .orderDetails(WynkOrderDetails.builder()
                            .type(existingOrder.getType())
                            .status(existingOrder.getStatus())
                            .statusDetail(orderStatusDetail)
                            .build())
                    .build();
        } else {
            return prepareAndPublish(request, clientDetails);
        }
    }

    private OrderPlacementResponse prepareAndPublish(OrderPlacementRequest request, ClientDetails clientDetails) {
        Order order = preparePartnerOrder(request, clientDetails);
        orderCacheService.save(order);
        return OrderPlacementResponse.builder()
                .orderId(order.getId())
                .msisdn(request.getMsisdn())
                .orderDetails(WynkOrderDetails.builder()
                        .statusDetail(OrderStatusDetail.ORDER_SUC201)
                        .status(OrderStatus.ACKNOWLEDGED)
                        .type(order.getType())
                        .build())
                .build();
    }

    private Order preparePartnerOrder(OrderPlacementRequest request, ClientDetails clientDetails) {
        OrderBuilder orderBuilder = Order.builder().partnerOrderId(request.getPartnerOrder().getId());
        String uid = MsisdnUtils.getUidFromMsisdn(request.getMsisdn());

        if (request instanceof OrderPlacementFallbackRequest) {
            orderBuilder.id(((OrderPlacementFallbackRequest) request).getFallbackOrderId());
        } else {
            orderBuilder.id(OrderIdentityGenerator.generate());
        }

        switch (request.getPartnerOrder().getType()) {
            case PURCHASE:
            case SUBSCRIBE:
                String paymentCode = request.getPartnerOrder().getType() == PaymentEvent.PURCHASE ? request.<FreshOrder>getPartnerOrder().getPaymentDetails().getGatewayName() : request.<RenewOrder>getPartnerOrder().getPaymentDetails().getGatewayName();
                orderBuilder.uid(uid)
                        .planId(request.getPartnerOrder().getPlanId())
                        .type(request.getPartnerOrder().getType().name())
                        .partnerAlias(clientDetails.getAlias())
                        .status(OrderStatus.ACKNOWLEDGED.name())
                        .createdOn(Calendar.getInstance())
                        .paymentCode(paymentCode)
                        .build();
                if (request.getPartnerOrder().getType() == PaymentEvent.PURCHASE) {
                    orderBuilder.amount(request.<FreshOrder>getPartnerOrder().getOrderDetails().getAmount())
                            .currency(request.<FreshOrder>getPartnerOrder().getOrderDetails().getCurrency());
                } else {
                    orderBuilder.amount(request.<RenewOrder>getPartnerOrder().getOrderDetails().getAmount())
                            .currency(request.<RenewOrder>getPartnerOrder().getOrderDetails().getCurrency());
                }
                break;
            case UNSUBSCRIBE:
                orderBuilder.uid(uid)
                        .planId(request.getPartnerOrder().getPlanId())
                        .type(request.getPartnerOrder().getType().name())
                        .partnerAlias(clientDetails.getAlias())
                        .status(OrderStatus.ACKNOWLEDGED.name())
                        .createdOn(Calendar.getInstance())
                        .build();
                break;
            default:
                throw new WynkRuntimeException(OrderErrorType.ORD001);
        }
        return orderBuilder.build();
    }

}
