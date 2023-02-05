package in.wynk.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@Builder
@AnalysedEntity
public class OrderAirtelResponse {

    @Analysed
    @JsonProperty("redirection_url")
    private final String redirectionUrl;
    @Analysed
    @JsonProperty("partner_correlation_id")
    private final String partnerCorrelationId;
    @Analysed
    @JsonProperty("product_key")
    private final String productKey;
    @Analysed
    @JsonProperty("creation_date")
    private final long creationDate;
    @Analysed
    @JsonProperty("start_date")
    private final long startDate;
    @Analysed
    @JsonProperty("end_date")
    private final long endDate;
    @Analysed
    @JsonProperty("status_message")
    private final String statusMessage;
    @Analysed
    @JsonProperty("status")
    private final String status;
    @Analysed
    @JsonProperty("deferred")
    private final boolean deferred;

    public static OrderAirtelResponse from(OrderResponse response) {
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD006));
        OrderAirtelResponseBuilder builder = OrderAirtelResponse.builder().partnerCorrelationId(response.getOrderId());
        if (response instanceof OrderFulfilmentResponse) {
            OrderFulfilmentResponse orderFulfilmentResponse = (OrderFulfilmentResponse) response;
            return builder
                    .statusMessage(orderFulfilmentResponse.getOrderDetails().getStatusDetail().getDescription())
                    .productKey(String.valueOf(orderFulfilmentResponse.getPlanDetails().getPlanId()))
                    .status(orderFulfilmentResponse.getOrderDetails().getStatus().name())
                    .creationDate(order.getCreatedOn().getTimeInMillis())
                    .build();
        } else {
            OrderNotificationResponse orderNotificationResponse = (OrderNotificationResponse) response;
            long endDate = 0;
            long startDate = 0;
            String finalOrderStatus = OrderConstant.FAILED;
            if (EnumSet.of(OrderStatus.DEFERRED, OrderStatus.FULFILLED).contains(orderNotificationResponse.getOrderDetails().getStatus())) {
                startDate = ((OrderNotificationResponse) response).getPlanDetails().getStartDate();
                endDate = ((OrderNotificationResponse) response).getPlanDetails().getEndDate();
                finalOrderStatus = OrderConstant.ACTIVE;
            }
            return builder
                    .productKey(String.valueOf(orderNotificationResponse.getPlanDetails().getPlanId()))
                    .statusMessage(orderNotificationResponse.getOrderDetails().getStatusDetail().getDescription())
                    .deferred(orderNotificationResponse.getOrderDetails().getStatus() == OrderStatus.DEFERRED)
                    .creationDate(order.getCreatedOn().getTimeInMillis())
                    .status(finalOrderStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
        }
    }

}
