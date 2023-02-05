package in.wynk.queue.poller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.constant.QueueLoggingMarker;
import in.wynk.queue.consumer.ISQSMessageConsumer;
import in.wynk.queue.extractor.ISQSMessageExtractor;
import in.wynk.queue.registry.IPollingQueueRegistryManager;
import in.wynk.queue.service.ISqsManagerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Getter
public abstract class AbstractSQSMessageConsumerPollingQueue<T> implements ISQSMessagePollingQueue, ISQSMessageConsumer<T>, InitializingBean {

    private final String queueName;
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;
    private final ISQSMessageExtractor messagesExtractor;
    private final ExecutorService handlerThreadPool;

    @Autowired
    private ISqsManagerService sqsManagerService;

    @Autowired
    @Qualifier(BeanConstant.SQS_POLLING_QUEUE_REGISTRY_MANAGER)
    private IPollingQueueRegistryManager pollingQueueRegistryManager;

    protected AbstractSQSMessageConsumerPollingQueue(String queueName,
                                                     AmazonSQS sqs,
                                                     ObjectMapper objectMapper,
                                                     ISQSMessageExtractor messagesExtractor,
                                                     ExecutorService handlerThreadPool) {
        this.queueName = queueName;
        this.sqs = sqs;
        this.objectMapper = objectMapper;
        this.handlerThreadPool = handlerThreadPool;
        this.messagesExtractor = messagesExtractor;
    }

    @Override
    public void afterPropertiesSet() {
        this.pollingQueueRegistryManager.register(this);
    }

    public abstract void start();

    public abstract void stop();

    protected void poll() {
        try {
            List<Message> messages = this.messagesExtractor.extract();
            for (Message message : messages) {
                log.info("Consuming queue: {} messageType: {} message: {}, ", queueName, messageType(), message.getBody());
                final T parsedMessage = objectMapper.readValue(
                        message.getBody(),
                        messageType());
                handlerThreadPool.submit(() -> {
                    try {
                        consume(parsedMessage);
                    } catch (Exception e) {
                        log.info("message {} with message attributes {}", parsedMessage, message.getAttributes());
                        sqsManagerService.publishSQSMessage(parsedMessage, message.getMessageAttributes());
                        if (!(e instanceof WynkRuntimeException)) {
                            log.error(QueueLoggingMarker.SQS_ERROR, "Something went wrong while processing message {} for queue: {}", message, queueName, e);
                        }
                    } finally {
                        flushMessage(message);
                    }
                });
            }
        } catch (Exception e) {
            log.error(QueueLoggingMarker.SQS_ERROR, "unable to poll queue: {}, due to {}", queueName, e.getMessage() , e);
        }
    }

    private void flushMessage(Message message) {
        sqs.deleteMessage(
                sqs.getQueueUrl(queueName).getQueueUrl(),
                message.getReceiptHandle()
        );
    }

}
