package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.PartnerOrder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderFulfilmentRequest extends OrderRequest {

    @Analysed
    @JsonProperty("order_id")
    private final String orderId;
    @Analysed
    @JsonProperty("partner_order")
    private final PartnerOrder partnerOrder;

    public <T extends PartnerOrder> T getPartnerOrder() {
        return (T) partnerOrder;
    }
}
