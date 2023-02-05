package in.wynk.order.service;

import in.wynk.order.dto.request.OrderDeferredFulfilRequest;
import in.wynk.order.dto.request.OrderFulfilmentRequest;
import in.wynk.order.dto.request.OrderNotificationRequest;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderResponse;

public interface IOrderManager {

    OrderResponse placeOrder(String partnerId, OrderPlacementRequest request);

    OrderResponse fulfilOrder(String partnerAlias, OrderFulfilmentRequest request);

    OrderResponse notifyOrder(String partnerAlias, OrderNotificationRequest request);

    OrderResponse orderStatus(String orderId, String msisdn);

    OrderResponse fulfilDeferredOrder(OrderDeferredFulfilRequest request);

}
