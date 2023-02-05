package in.wynk.order.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.event.OrderMessageThresholdExceedEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class ScheduleOrderDeferredMessageThresholdExceedEvent extends OrderMessageThresholdExceedEvent {

    @Analysed
    @JsonProperty("is_pre_fulfilled")
    private final boolean preFulfilled;
    @Analysed
    @JsonProperty("deferred_until")
    private final long deferredUntil;
    @Analysed
    @JsonProperty("valid_until")
    private final long validUntil;

}
