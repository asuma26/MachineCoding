package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderCancellationRequest extends OrderRequest {

    @Analysed
    @JsonProperty("order_id")
    private final String orderId;
    @Analysed
    @JsonProperty("plan_id")
    private final int planId;
    @Analysed
    @JsonProperty("mobile_no")
    private final String msisdn;
    @Analysed
    @JsonProperty("cancellation_reason")
    private final String reason;

}
