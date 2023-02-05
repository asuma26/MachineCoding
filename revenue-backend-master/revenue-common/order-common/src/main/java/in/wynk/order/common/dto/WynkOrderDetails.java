package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AnalysedEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WynkOrderDetails {

    @Analysed
    @JsonProperty("order_type")
    private final PaymentEvent type;
    @Analysed
    @JsonProperty("order_status_code")
    private final OrderStatusDetail statusDetail;
    @Setter
    @Analysed
    @JsonProperty("order_status")
    private OrderStatus status;

}
