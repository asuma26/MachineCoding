package in.wynk.order.common.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.PartnerOrder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@AnalysedEntity
public class OrderFulfilmentMessageThresholdExceedEvent extends OrderMessageThresholdExceedEvent {

    @Analysed
    @JsonProperty("partner_order")
    private final PartnerOrder partnerOrder;
    @Analysed
    @JsonProperty("plan_details")
    private final String planDetails;

}
