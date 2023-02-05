package in.wynk.order.common.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.annotation.analytic.core.annotations.Analysed;
import in.wynk.order.common.dto.PartnerOrder;
import in.wynk.order.common.event.OrderPlacementMessageThresholdExceedEvent;
import in.wynk.queue.dto.FIFOQueueMessageMarker;
import in.wynk.queue.dto.MessageToEventMapper;
import in.wynk.queue.dto.QueueType;
import in.wynk.queue.dto.WynkQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@WynkQueue(queueName = "${order.pooling.queue.placement.name}", delaySeconds = "${order.pooling.queue.placement.sqs.producer.delayInSecond}", queueType = QueueType.FIFO)
public class OrderPlacementMessage extends OrderMessage implements FIFOQueueMessageMarker, MessageToEventMapper<OrderPlacementMessageThresholdExceedEvent> {
    @Analysed
    private PartnerOrder partnerOrder;

    @Override
    @JsonIgnore
    public String getMessageGroupId() {
        return getOrderId();
    }

    @Override
    @JsonIgnore
    public String getMessageDeDuplicationId() {
        return getOrderId();
    }

    @Override
    public OrderPlacementMessageThresholdExceedEvent map() {
        WynkQueue queueData = this.getClass().getAnnotation(WynkQueue.class);
        return OrderPlacementMessageThresholdExceedEvent.builder()
                .maxAttempt(queueData.maxRetryCount())
                .callbackUrl(getCallbackUrl())
                .partnerOrder(partnerOrder)
                .partnerId(getPartnerId())
                .orderId(getOrderId())
                .msisdn(getMsisdn())
                .build();
    }
}
