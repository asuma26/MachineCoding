package in.wynk.order.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.message.OrderMessage;
import in.wynk.order.event.ScheduleOrderDeferredMessageThresholdExceedEvent;
import in.wynk.queue.dto.MessageToEventMapper;
import in.wynk.queue.dto.WynkQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
@WynkQueue(queueName = "${order.pooling.queue.deferred.name}", delaySeconds = "${order.pooling.queue.deferred.sqs.producer.delayInSecond}")
public class DeferredOrdersMessage extends OrderMessage implements MessageToEventMapper<ScheduleOrderDeferredMessageThresholdExceedEvent> {
    @Analysed
    @JsonProperty("is_pre_fulfilled")
    private boolean preFulfilled;
    @Analysed
    @JsonProperty("deferred_until")
    private long deferredUntil;
    @Analysed
    @JsonProperty("valid_until")
    private long validUntil;


    @Override
    public ScheduleOrderDeferredMessageThresholdExceedEvent map() {
        WynkQueue queueData = this.getClass().getAnnotation(WynkQueue.class);
        return ScheduleOrderDeferredMessageThresholdExceedEvent.builder()
                .maxAttempt(queueData.maxRetryCount())
                .deferredUntil(getDeferredUntil())
                .preFulfilled(isPreFulfilled())
                .callbackUrl(getCallbackUrl())
                .validUntil(getValidUntil())
                .partnerId(getPartnerId())
                .orderId(getOrderId())
                .msisdn(getMsisdn())
                .build();
    }
}
