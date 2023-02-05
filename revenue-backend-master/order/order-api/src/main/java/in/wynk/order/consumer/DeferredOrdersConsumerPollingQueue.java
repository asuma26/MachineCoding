package in.wynk.order.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.dto.message.DeferredOrdersMessage;
import in.wynk.order.dto.request.OrderDeferredFulfilRequest;
import in.wynk.order.dto.response.OrderResponse;
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
public class DeferredOrdersConsumerPollingQueue extends AbstractSQSMessageConsumerPollingQueue<DeferredOrdersMessage> {
    private final ExecutorService messageHandlerThreadPool;
    private final ScheduledExecutorService pollingThreadPool;
    @Value("${order.pooling.queue.deferred.enabled}")
    private boolean deferringOrdersEnabled;
    @Value("${order.pooling.queue.deferred.sqs.consumer.delay}")
    private long deferringPoolingDelay;
    @Value("${order.pooling.queue.deferred.sqs.consumer.delayTimeUnit}")
    private TimeUnit deferringPoolingDelayTimeUnit;
    @Autowired
    private IOrderManager orderManager;

    public DeferredOrdersConsumerPollingQueue(String queueName,
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
    @AnalyseTransaction(name = "DeferredOrderAsync")
    public void consume(DeferredOrdersMessage message) {
        AnalyticService.update(message);
        log.info(OrderLoggingMarker.DEFERRED_ORDER_QUEUE, "processing DeferredOrdersMessage for orderid {}", message.getOrderId());
        OrderResponse orderResponse = orderManager.fulfilDeferredOrder(OrderDeferredFulfilRequest.builder()
                .deferredUntil(message.getDeferredUntil())
                .preFulfilled(message.isPreFulfilled())
                .callbackUrl(message.getCallbackUrl())
                .validUntil(message.getValidUntil())
                .orderId(message.getOrderId())
                .build());
        AnalyticService.update(orderResponse);
    }

    @Override
    public Class<DeferredOrdersMessage> messageType() {
        return DeferredOrdersMessage.class;
    }

    @Override
    public void start() {
        if (deferringOrdersEnabled) {
            log.info("Starting DeferredOrdersConsumerPollingQueue...");
            pollingThreadPool.scheduleWithFixedDelay(
                    this::poll,
                    0,
                    deferringPoolingDelay,
                    deferringPoolingDelayTimeUnit
            );
        }
    }

    @Override
    public void stop() {
        if (deferringOrdersEnabled) {
            log.info("Shutting down DeferredOrdersConsumerPollingQueue ...");
            pollingThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdown();
        }
    }
}
