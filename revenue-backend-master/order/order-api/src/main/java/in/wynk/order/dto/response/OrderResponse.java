package in.wynk.order.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public abstract class OrderResponse {

    @Analysed
    @JsonIgnore
    private final String msisdn;
    @Analysed
    @JsonProperty("wynk_order_id")
    private final String orderId;
    @Analysed
    @JsonProperty("partner_order_id")
    private final String partnerOrderId;

}
