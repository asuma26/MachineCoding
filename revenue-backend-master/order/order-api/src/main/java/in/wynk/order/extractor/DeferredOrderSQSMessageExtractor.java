package in.wynk.order.extractor;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.extractor.AbstractSQSMessageExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class DeferredOrderSQSMessageExtractor extends AbstractSQSMessageExtractor {
    private final String queueName;
    @Value("${order.pooling.queue.deferred.sqs.messages.extractor.batchSize}")
    private int batchSize;
    @Value("${order.pooling.queue.deferred.sqs.messages.extractor.waitTimeInSeconds}")
    private int waitTimeInSeconds;

    public DeferredOrderSQSMessageExtractor(String queueName,
                                            @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqs) {
        super(sqs);
        this.queueName = queueName;
    }

    @Override
    public ReceiveMessageRequest buildReceiveMessageRequest() {
        return new ReceiveMessageRequest()
                .withMaxNumberOfMessages(batchSize)
                .withQueueUrl(getSqs().getQueueUrl(queueName).getQueueUrl())
                .withWaitTimeSeconds(waitTimeInSeconds);
    }

}
