package in.wynk.payment.common.messages;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.queue.dto.QueueType;
import in.wynk.queue.dto.WynkQueue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
@WynkQueue(queueName = "${payment.pooling.queue.unschedule.name}", delaySeconds = "${payment.pooling.queue.unschedule.sqs.producer.delayInSecond}", queueType = QueueType.STANDARD)
public class PaymentRecurringUnSchedulingMessage {

    @Analysed
    private int planId;
    @Analysed
    private String uid;
    @Analysed
    private String transactionId;
    @Analysed
    private long deferredUntil;
    @Analysed
    private long validUntil;
    @Analysed
    private PaymentEvent paymentEvent;

}
