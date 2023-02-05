package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class PartnerPaymentDetails {

    @Analysed
    private final String gatewayId;
    @Analysed
    private final String gatewayName;
    @Analysed
    private final long timestamp;

    @JsonCreator
    public PartnerPaymentDetails(@JsonProperty("payment_gateway_id") String gatewayId, @JsonProperty("payment_gateway_name") String gatewayName, @JsonProperty("payment_timestamp") long timestamp) {
        this.gatewayId = gatewayId;
        this.gatewayName = gatewayName;
        this.timestamp = timestamp;
    }
}
