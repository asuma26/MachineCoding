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
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Map;

@Getter
@Builder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
@WynkQueue(queueName = "${payment.pooling.queue.schedule.name}", delaySeconds = "${payment.pooling.queue.schedule.sqs.producer.delayInSecond}", queueType = QueueType.STANDARD)
public class PaymentRecurringSchedulingMessage {

    @Analysed
    private int planId;
    @Analysed
    private String uid;
    @Analysed
    private String msisdn;
    @Analysed
    private String clientAlias;
    @Analysed
    private String paymentCode;
    @Analysed
    private PaymentEvent event;
    @Analysed
    private Date nextChargingDate;
    @Analysed
    private Map<String, String> paymentMetaData;

}
