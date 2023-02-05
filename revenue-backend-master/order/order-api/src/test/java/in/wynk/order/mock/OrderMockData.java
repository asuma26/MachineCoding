package in.wynk.order.mock;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.order.common.dto.*;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.enums.ProvisionState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

public class OrderMockData {

    public static PlanProvisioningResponse buildSubscriptionProvisioningResponse() {
        return PlanProvisioningResponse.builder()
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .state(ProvisionState.SUBSCRIBED)
                .autoRenew(true)
                .endDate(System.currentTimeMillis() + 31 * 24 * 60 * 60)
                .build();
    }

    public static PlanProvisioningResponse buildUnSubscriptionProvisioningResponse() {
        return PlanProvisioningResponse.builder()
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .state(ProvisionState.UNSUBSCRIBED)
                .autoRenew(false)
                .endDate(System.currentTimeMillis() + 31 * 24 * 60 * 60)
                .build();
    }

    public static PlanProvisioningResponse buildDeferredSubscriptionProvisioningResponse() {
        return PlanProvisioningResponse.builder()
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .state(ProvisionState.DEFERRED)
                .autoRenew(true)
                .endDate(System.currentTimeMillis() + 31 * 24 * 60 * 60)
                .build();
    }

    public static OrderPlacementRequest buildFreshOrderPlacementRequest() {
        return OrderPlacementRequest.builder()
                .msisdn(OrderMockConstants.MSISDN)
                .partnerOrder(FreshOrder.builder()
                        .id("paytm-xyz-order-reference-id-temp")
                        .planId(OrderMockConstants.SELECTED_PLAN_ID)
                        .orderDetails(PartnerOrderDetails.builder()
                                .customerId("zovi")
                                .amount(200)
                                .cashbackOffered(10)
                                .discountOffered(0)
                                .currency("INR")
                                .build())
                        .paymentDetails(PartnerPaymentDetails.builder()
                                .gatewayId("ICICI")
                                .gatewayName("ICICI")
                                .build())
                        .build()
                ).build();
    }

    public static OrderPlacementRequest buildRenewOrderPlacementRequest() {
        return OrderPlacementRequest.builder()
                .msisdn(OrderMockConstants.MSISDN)
                .partnerOrder(RenewOrder.builder()
                        .id("paytm-xyz-order-reference-id-temp")
                        .planId(OrderMockConstants.SELECTED_PLAN_ID)
                        .orderDetails(PartnerOrderDetails.builder()
                                .customerId("zovi")
                                .amount(200)
                                .cashbackOffered(10)
                                .discountOffered(0)
                                .currency("INR")
                                .build())
                        .paymentDetails(PartnerPaymentDetails.builder()
                                .gatewayId("ICICI")
                                .gatewayName("ICICI")
                                .build())
                        .build()
                ).build();
    }

    public static OrderPlacementRequest buildCancellationOrderPlacementRequest() {
        return OrderPlacementRequest.builder()
                .msisdn(OrderMockConstants.MSISDN)
                .partnerOrder(CancellationOrder.builder()
                        .id("paytm-xyz-order-reference-id-temp")
                        .planId(OrderMockConstants.SELECTED_PLAN_ID)
                        .build()
                ).build();
    }

    public static Order buildPersistableAcknowledgedOrder() {
        return Order.builder()
                .id(OrderMockConstants.MOCK_ORDER_ID)
                .uid(OrderMockConstants.UID)
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .type(PaymentEvent.PURCHASE.name())
                .status(OrderStatus.ACKNOWLEDGED.name())
                .amount(OrderMockConstants.ORDER_AMOUNT)
                .currency(OrderMockConstants.CURRENCY)
                .createdOn(Calendar.getInstance())
                .build();
    }

    public static Order buildRenewPersistableAcknowledgedOrder() {
        return Order.builder()
                .id(OrderMockConstants.MOCK_ORDER_ID)
                .uid(OrderMockConstants.UID)
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .type(PaymentEvent.SUBSCRIBE.name())
                .status(OrderStatus.ACKNOWLEDGED.name())
                .amount(OrderMockConstants.ORDER_AMOUNT)
                .currency(OrderMockConstants.CURRENCY)
                .createdOn(Calendar.getInstance())
                .build();
    }

    public static Order buildRenewPersistableFulfilledOrder() {
        return Order.builder()
                .id(OrderMockConstants.MOCK_ORDER_ID)
                .uid(OrderMockConstants.UID)
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .type(PaymentEvent.SUBSCRIBE.name())
                .status(OrderStatus.ACKNOWLEDGED.name())
                .amount(OrderMockConstants.ORDER_AMOUNT)
                .currency(OrderMockConstants.CURRENCY)
                .createdOn(Calendar.getInstance())
                .updatedOn(Calendar.getInstance())
                .build();
    }

    public static Order buildCancellationPersistableFulfilledOrder() {
        return Order.builder()
                .id(OrderMockConstants.MOCK_ORDER_ID)
                .uid(OrderMockConstants.UID)
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .type(PaymentEvent.UNSUBSCRIBE.name())
                .status(OrderStatus.FULFILLED.name())
                .amount(OrderMockConstants.ORDER_AMOUNT)
                .currency(OrderMockConstants.CURRENCY)
                .createdOn(Calendar.getInstance())
                .updatedOn(Calendar.getInstance())
                .build();
    }

    public static Order buildPersistableFulfilledOrder() {
        return Order.builder()
                .id(OrderMockConstants.MOCK_ORDER_ID)
                .uid(OrderMockConstants.UID)
                .planId(OrderMockConstants.SELECTED_PLAN_ID)
                .type(PaymentEvent.PURCHASE.name())
                .status(OrderStatus.FULFILLED.name())
                .amount(OrderMockConstants.ORDER_AMOUNT)
                .currency(OrderMockConstants.CURRENCY)
                .createdOn(Calendar.getInstance())
                .updatedOn(Calendar.getInstance())
                .build();
    }

    public static Stream<DeferredOrderDetail> buildDeferredOrdersData() {
        List<DeferredOrderDetail> mockDeferredDetails = new ArrayList<>();
        DeferredOrderDetail deferredOrderDetail1 = DeferredOrderDetail.
                builder().
                status(OrderStatus.DEFERRED.name())
                .createdOn(Calendar.getInstance())
                .id("123")
                .build();
        DeferredOrderDetail deferredOrderDetail2 = DeferredOrderDetail.
                builder().
                status(OrderStatus.FULFILLED.name())
                .createdOn(Calendar.getInstance())
                .updatedOn(Calendar.getInstance())
                .id("456")
                .build();
        mockDeferredDetails.add(deferredOrderDetail1);
        mockDeferredDetails.add(deferredOrderDetail2);
        return mockDeferredDetails.stream();
    }

}
