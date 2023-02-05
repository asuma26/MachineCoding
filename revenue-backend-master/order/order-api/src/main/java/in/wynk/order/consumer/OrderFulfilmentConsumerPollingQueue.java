package in.wynk.order.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.order.common.message.OrderFulfilmentMessage;
import in.wynk.order.core.constant.ExecutionType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.dto.request.OrderFulfilmentFallbackRequest;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.service.IOrderManager;
import in.wynk.queue.extractor.ISQSMessageExtractor;
import in.wynk.queue.poller.AbstractSQSMessageConsumerPollingQueue;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderFulfilmentConsumerPollingQueue extends AbstractSQSMessageConsumerPollingQueue<OrderFulfilmentMessage> {

    @Value("${order.pooling.queue.fulfilment.enabled}")
    private boolean fulfilmentPollingQueueEnable;
    @Value("${order.pooling.queue.fulfilment.sqs.consumer.delay}")
    private long fulfilmentPollingQueueDelay;
    @Value("${order.pooling.queue.fulfilment.sqs.consumer.delayTimeUnit}")
    private TimeUnit fulfilmentPollingDelayTimeUnit;

    private final ExecutorService messageHandlerThreadPool;
    private final ScheduledExecutorService pollingThreadPool;

    @Autowired
    private IOrderManager orderManager;

    public OrderFulfilmentConsumerPollingQueue(String queueName,
                                               AmazonSQS sqs,
                                               ObjectMapper objectMapper,
                                               ISQSMessageExtractor messagesExtractor,
                                               ExecutorService messageHandlerThreadPool,
                                               ScheduledExecutorService pollingThreadPool) {
        super(queueName, sqs, objectMapper, messagesExtractor, messageHandlerThreadPool);
        this.pollingThreadPool = pollingThreadPool;
        this.messageHandlerThreadPool = messageHandlerThreadPool;
    }

    @SneakyThrows
    @Override
    @AnalyseTransaction(name = "fulfilOrderAsync")
    public void consume(OrderFulfilmentMessage message) {
        AnalyticService.update(message);
        log.info(OrderLoggingMarker.ORDER_PLACEMENT_QUEUE, "processing OrderFulfilmentMessage for msisdn {} and partnerId {}", message.getMsisdn(), message.getPartnerId());
        OrderResponse orderResponse = orderManager.fulfilOrder(message.getPartnerId(), OrderFulfilmentFallbackRequest.builder()
                .msisdn(message.getMsisdn())
                .orderId(message.getOrderId())
                .callbackUrl(message.getCallbackUrl())
                .planResponse(getObjectMapper().readValue(message.getPlanDetails(), PlanProvisioningResponse.class))
                .partnerOrder(message.getPartnerOrder())
                .executionType(ExecutionType.ASYNC)
                .build());
        AnalyticService.update(orderResponse);
    }

    @Override
    public Class<OrderFulfilmentMessage> messageType() {
        return OrderFulfilmentMessage.class;
    }

    @Override
    public void start() {
        if (fulfilmentPollingQueueEnable) {
            log.info("Starting OrderFulfilmentConsumerPollingQueue...");
            pollingThreadPool.scheduleWithFixedDelay(
                    this::poll,
                    0,
                    fulfilmentPollingQueueDelay,
                    fulfilmentPollingDelayTimeUnit
            );
        }
    }

    @Override
    public void stop() {
        if (fulfilmentPollingQueueEnable) {
            log.info("Shutting down OrderFulfilmentConsumerPollingQueue ...");
            pollingThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdown();
        }
    }
}
