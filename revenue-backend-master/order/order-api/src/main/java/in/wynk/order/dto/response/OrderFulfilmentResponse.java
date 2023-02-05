package in.wynk.order.dto.response;

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
public class OrderFulfilmentResponse extends OrderResponse {

    @Analysed
    @JsonProperty("wynk_order_details")
    private final WynkOrderDetails orderDetails;
    @Analysed
    @JsonProperty("wynk_subscription_details")
    private final WynkPlanDetails planDetails;

}
