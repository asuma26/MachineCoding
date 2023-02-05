package in.wynk.order.common.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.PartnerOrder;
import in.wynk.order.common.event.OrderFulfilmentMessageThresholdExceedEvent;
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
@WynkQueue(queueName = "${order.pooling.queue.fulfilment.name}", delaySeconds = "${order.pooling.queue.fulfilment.sqs.producer.delayInSecond}")
public class OrderFulfilmentMessage extends OrderMessage implements MessageToEventMapper<OrderFulfilmentMessageThresholdExceedEvent> {
    @Analysed
    @JsonProperty("partner_order")
    private PartnerOrder partnerOrder;
    @Analysed
    @JsonProperty("plan_details")
    private String planDetails;

    @Override
    public OrderFulfilmentMessageThresholdExceedEvent map() {
        WynkQueue queueData = this.getClass().getAnnotation(WynkQueue.class);
        return OrderFulfilmentMessageThresholdExceedEvent.builder()
                .maxAttempt(queueData.maxRetryCount())
                .callbackUrl(getCallbackUrl())
                .partnerOrder(partnerOrder)
                .partnerId(getPartnerId())
                .planDetails(planDetails)
                .orderId(getOrderId())
                .msisdn(getMsisdn())
                .build();
    }
}
