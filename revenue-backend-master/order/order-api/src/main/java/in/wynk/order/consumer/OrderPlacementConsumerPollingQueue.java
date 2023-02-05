package in.wynk.order.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.order.common.message.OrderPlacementMessage;
import in.wynk.order.core.constant.ExecutionType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.dto.request.OrderPlacementFallbackRequest;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.service.IOrderManager;
import in.wynk.queue.extractor.ISQSMessageExtractor;
import in.wynk.queue.poller.AbstractSQSMessageConsumerPollingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderPlacementConsumerPollingQueue extends AbstractSQSMessageConsumerPollingQueue<OrderPlacementMessage> {

    @Value("${order.pooling.queue.placement.enabled}")
    private boolean placementPollingQueueEnable;
    @Value("${order.pooling.queue.placement.sqs.consumer.delay}")
    private long placementPollingQueueDelay;
    @Value("${order.pooling.queue.placement.sqs.consumer.delayTimeUnit}")
    private TimeUnit placementPollingDelayTimeUnit;

    private final ExecutorService messageHandlerThreadPool;
    private final ScheduledExecutorService pollingThreadPool;

    @Autowired
    private IOrderManager orderManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public OrderPlacementConsumerPollingQueue(String queueName,
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
    @AnalyseTransaction(name = "placeOrderAsync")
    public void consume(OrderPlacementMessage message) {
        AnalyticService.update(message);
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_QUEUE, "processing OrderPlacementMessage for msisdn {} and partnerId {}", message.getMsisdn(), message.getPartnerId());
        OrderResponse orderResponse = orderManager.placeOrder(message.getPartnerId(), OrderPlacementFallbackRequest.builder()
                .partnerOrder(message.getPartnerOrder())
                .callbackUrl(message.getCallbackUrl())
                .fallbackOrderId(message.getOrderId())
                .executionType(ExecutionType.ASYNC)
                .msisdn(message.getMsisdn())
                .build());

        AnalyticService.update(orderResponse);
    }

    @Override
    public Class<OrderPlacementMessage> messageType() {
        return OrderPlacementMessage.class;
    }

    @Override
    public void start() {
        if (placementPollingQueueEnable) {
            log.info("Starting OrderPlacementConsumerPollingQueue...");
            pollingThreadPool.scheduleWithFixedDelay(
                    this::poll,
                    0,
                    placementPollingQueueDelay,
                    placementPollingDelayTimeUnit
            );
        }
    }

    @Override
    public void stop() {
        if (placementPollingQueueEnable) {
            log.info("Shutting down OrderPlacementConsumerPollingQueue ...");
            pollingThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdown();
        }
    }
}
