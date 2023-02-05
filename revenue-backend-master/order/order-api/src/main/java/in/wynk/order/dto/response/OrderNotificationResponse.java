package in.wynk.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.dto.WynkPlanDetails;
import in.wynk.order.mapper.IOrderCallbackMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@AnalysedEntity
public class OrderNotificationResponse extends OrderResponse implements IOrderCallbackMapper<OrderNotificationResponse, OrderNotificationResponse> {

    @Analysed
    @JsonProperty("wynk_order_details")
    private final WynkOrderDetails orderDetails;
    @Analysed
    @JsonProperty("wynk_subscription_details")
    private final WynkPlanDetails planDetails;

    @Override
    public OrderNotificationResponse from(OrderNotificationResponse response) {
        return this;
    }
}
