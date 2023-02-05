package in.wynk.queue.service.impl;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.constant.QueueErrorType;
import in.wynk.queue.dto.*;
import in.wynk.queue.producer.SQSMessagePublisher;
import in.wynk.queue.service.ISqsManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static in.wynk.queue.constant.BeanConstant.NUMBER;
import static in.wynk.queue.constant.BeanConstant.RETRY_COUNT;

@Service
@Slf4j
public class SqsManagerServiceImpl implements ISqsManagerService {

    private final SQSMessagePublisher sqsMessagePublisher;
    private final ConfigurableBeanFactory configurableBeanFactory;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SqsManagerServiceImpl(SQSMessagePublisher sqsMessagePublisher, ConfigurableBeanFactory configurableBeanFactory, ApplicationEventPublisher applicationEventPublisher) {
        this.sqsMessagePublisher = sqsMessagePublisher;
        this.configurableBeanFactory = configurableBeanFactory;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private String getProperty(String property) {
        return configurableBeanFactory.resolveEmbeddedValue(property);
    }

    @Override
    public <T> void publishSQSMessage(T message) {
        this.publishSQSMessage(message, new HashMap<>());
    }

    @Override
    public <T> void publishSQSMessage(T message, Map<String, MessageAttributeValue> messageAttributes) {
        log.debug("Publishing message: {}", message);
        try {
            if (!message.getClass().isAnnotationPresent(WynkQueue.class)) {
                throw new WynkRuntimeException(QueueErrorType.SQS006, "Queue Message must be annotated by WynkQueue");
            }
            WynkQueue queueData = message.getClass().getAnnotation(WynkQueue.class);
            int count = Integer.parseInt(messageAttributes
                    .getOrDefault(RETRY_COUNT, new MessageAttributeValue().withStringValue("0"))
                    .getStringValue());
            if (queueData.maxRetryCount() < count) {
                applicationEventPublisher.publishEvent(message);
                if (message instanceof MessageToEventMapper<?>) {
                    applicationEventPublisher.publishEvent(((MessageToEventMapper<?>) message).map());
                } else {
                    applicationEventPublisher.publishEvent(MessageToEventMapper.map(message));
                }
                return;
            }
            String queueName = getProperty(queueData.queueName());
            Integer delaySeconds = Integer.parseInt(getProperty(queueData.delaySeconds()));
            messageAttributes.put(RETRY_COUNT, new MessageAttributeValue()
                    .withDataType(NUMBER)
                    .withStringValue(String.valueOf(count + 1)));
            if (queueData.queueType() == QueueType.FIFO) {
                if (message instanceof FIFOQueueMessageMarker) {
                    sqsMessagePublisher.publish(SendSQSMessageRequest.<T>builder()
                            .message(message)
                            .queueName(queueName)
                            .messageAttributes(messageAttributes)
                            .messageGroupId(((FIFOQueueMessageMarker) message).getMessageGroupId())
                            .messageDeduplicationId(((FIFOQueueMessageMarker) message).getMessageDeDuplicationId())
                            .build());
                } else {
                    throw new WynkRuntimeException(QueueErrorType.SQS006);
                }
            } else {
                sqsMessagePublisher.publish(SendSQSMessageRequest.<T>builder()
                        .message(message)
                        .queueName(queueName)
                        .delaySeconds(delaySeconds)
                        .messageAttributes(messageAttributes)
                        .build());
            }
        } catch (Exception e) {
            throw new WynkRuntimeException(QueueErrorType.SQS001, e);
        }
    }

}