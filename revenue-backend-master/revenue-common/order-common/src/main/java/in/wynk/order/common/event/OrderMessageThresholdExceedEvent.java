package in.wynk.order.common.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.queue.dto.MessageThresholdExceedEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public abstract class OrderMessageThresholdExceedEvent extends MessageThresholdExceedEvent {
    @Analysed
    @JsonProperty("order_id")
    private final String orderId;
    @Analysed
    @JsonProperty("mobile_no")
    private final String msisdn;
    @Analysed
    @JsonProperty("partner_id")
    private final String partnerId;
    @Analysed
    @JsonProperty("callback_url")
    private final String callbackUrl;

    @Override
    public String getType() {
        return this.getClass().getCanonicalName();
    }

}
