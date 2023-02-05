package in.wynk.order.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.order.common.message.OrderNotificationMessage;
import in.wynk.order.core.constant.ExecutionType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.dto.request.OrderNotificationRequest;
import in.wynk.order.service.IOrderManager;
import in.wynk.queue.extractor.ISQSMessageExtractor;
import in.wynk.queue.poller.AbstractSQSMessageConsumerPollingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderNotificationConsumerPollingQueue extends AbstractSQSMessageConsumerPollingQueue<OrderNotificationMessage> {

    @Value("${order.pooling.queue.notification.enabled}")
    private boolean notificationPollingQueueEnable;
    @Value("${order.pooling.queue.notification.sqs.consumer.delay}")
    private long notificationPollingQueueDelay;
    @Value("${order.pooling.queue.notification.sqs.consumer.delayTimeUnit}")
    private TimeUnit notificationPollingDelayTimeUnit;

    private final ExecutorService messageHandlerThreadPool;
    private final ScheduledExecutorService pollingThreadPool;

    @Autowired
    private IOrderManager orderManager;

    public OrderNotificationConsumerPollingQueue(String queueName,
                                                 AmazonSQS sqs,
                                                 ObjectMapper objectMapper,
                                                 ISQSMessageExtractor messagesExtractor,
                                                 ExecutorService messageHandlerThreadPool,
                                                 ScheduledExecutorService pollingThreadPool) {
        super(queueName, sqs, objectMapper, messagesExtractor, messageHandlerThreadPool);
        this.pollingThreadPool = pollingThreadPool;
        this.messageHandlerThreadPool = messageHandlerThreadPool;
    }

    @Override
    @AnalyseTransaction(name = "notifyOrderAsync")
    public void consume(OrderNotificationMessage message) {
        AnalyticService.update(message);
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_QUEUE, "processing OrderNotificationMessage for msisdn {} and partnerId {}", message.getMsisdn(), message.getPartnerId());
        orderManager.notifyOrder(message.getPartnerId(), OrderNotificationRequest.builder()
                .msisdn(message.getMsisdn())
                .orderId(message.getOrderId())
                .callbackUrl(message.getCallbackUrl())
                .planDetails(message.getPlanDetails())
                .orderDetails(message.getOrderDetails())
                .executionType(ExecutionType.ASYNC)
                .build());
    }

    @Override
    public Class<OrderNotificationMessage> messageType() {
        return OrderNotificationMessage.class;
    }

    @Override
    public void start() {
        if (notificationPollingQueueEnable) {
            log.info("Starting OrderNotificationConsumerPollingQueue...");
            pollingThreadPool.scheduleWithFixedDelay(
                    this::poll,
                    0,
                    notificationPollingQueueDelay,
                    notificationPollingDelayTimeUnit
            );
        }
    }

    @Override
    public void stop() {
        if (notificationPollingQueueEnable) {
            log.info("Shutting down OrderNotificationConsumerPollingQueue ...");
            pollingThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdown();
        }
    }
}
