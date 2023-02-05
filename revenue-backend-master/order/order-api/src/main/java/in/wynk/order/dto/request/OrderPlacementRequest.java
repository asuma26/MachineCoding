package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.PartnerOrder;
import in.wynk.order.core.constant.ExecutionType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderPlacementRequest extends OrderRequest {

    @Analysed
    @JsonProperty("partner_order")
    private final PartnerOrder partnerOrder;

    @JsonCreator
    public OrderPlacementRequest(@JsonProperty("mobile_no") String msisdn, @JsonProperty("callback_url") String callbackUrl, @JsonProperty("partner_order") PartnerOrder partnerOrder) {
        super(msisdn, callbackUrl, ExecutionType.SYNC);
        this.partnerOrder = partnerOrder;
    }

    public <T extends PartnerOrder> T getPartnerOrder() {
        return (T) partnerOrder;
    }

}
