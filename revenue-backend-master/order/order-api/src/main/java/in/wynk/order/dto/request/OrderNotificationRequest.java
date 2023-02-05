package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.dto.WynkPlanDetails;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderNotificationRequest extends OrderRequest {

    @Analysed
    @JsonProperty("order_id")
    private final String orderId;
    @Analysed
    @JsonProperty("wynk_order_details")
    private final WynkOrderDetails orderDetails;
    @Analysed
    @JsonProperty("wynk_subscription_details")
    private final WynkPlanDetails planDetails;

}
