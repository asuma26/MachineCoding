package in.wynk.order.extractor;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.extractor.AbstractSQSMessageExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static in.wynk.queue.constant.BeanConstant.ALL;

public class OrderNotificationSQSMessageExtractor extends AbstractSQSMessageExtractor {

    @Value("${order.pooling.queue.notification.sqs.messages.extractor.batchSize}")
    private int batchSize;

    @Value("${order.pooling.queue.notification.sqs.messages.extractor.waitTimeInSeconds}")
    private int waitTimeInSeconds;

    private final String queueName;

    public OrderNotificationSQSMessageExtractor(String queueName,
                                             @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqs) {
        super(sqs);
        this.queueName = queueName;
    }

    @Override
    public ReceiveMessageRequest buildReceiveMessageRequest() {
        return new ReceiveMessageRequest()
                .withMessageAttributeNames(ALL)
                .withMaxNumberOfMessages(batchSize)
                .withQueueUrl(getSqs().getQueueUrl(queueName).getQueueUrl())
                .withWaitTimeSeconds(waitTimeInSeconds);
    }

}
