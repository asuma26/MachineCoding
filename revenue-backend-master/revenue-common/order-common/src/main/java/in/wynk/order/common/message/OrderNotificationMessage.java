package in.wynk.order.common.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.dto.WynkPlanDetails;
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
@WynkQueue(queueName = "${order.pooling.queue.notification.name}", delaySeconds = "${order.pooling.queue.notification.sqs.producer.delayInSecond}")
public class OrderNotificationMessage extends OrderMessage {
    @Analysed
    @JsonProperty("wynk_order_details")
    private WynkOrderDetails orderDetails;
    @Analysed
    @JsonProperty("wynk_subscription_details")
    private WynkPlanDetails planDetails;
}
