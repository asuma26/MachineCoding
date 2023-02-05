package in.wynk.subscription.common.message;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.enums.TransactionStatus;
import in.wynk.queue.dto.WynkQueue;
import lombok.*;

@Getter
@Builder
@ToString
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor
@WynkQueue(queueName = "${subscription.pooling.queue.provision.name}", delaySeconds = "${subscription.pooling.queue.provision.sqs.producer.delayInSecond}")
public class SubscriptionProvisioningMessage {

    @Analysed
    private int planId;
    @Analysed
    private String uid;
    @Analysed
    private String msisdn;
    @Analysed
    private String paymentCode;
    @Analysed
    private String callbackUrl;
    @Analysed
    private String referenceId;
    @Analysed
    private String paymentPartner;
    @Analysed
    private TransactionStatus transactionStatus;
    @Analysed
    private PaymentEvent paymentEvent;

}
