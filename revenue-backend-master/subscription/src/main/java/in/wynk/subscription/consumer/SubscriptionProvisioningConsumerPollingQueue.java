package in.wynk.subscription.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.order.common.dto.CancellationOrder;
import in.wynk.order.common.dto.FreshOrder;
import in.wynk.order.common.dto.RenewOrder;
import in.wynk.order.common.message.OrderFulfilmentMessage;
import in.wynk.order.common.message.OrderFulfilmentMessage.OrderFulfilmentMessageBuilder;
import in.wynk.queue.extractor.ISQSMessageExtractor;
import in.wynk.queue.poller.AbstractSQSMessageConsumerPollingQueue;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import in.wynk.subscription.common.dto.ProvisioningResponse;
import in.wynk.subscription.common.dto.SinglePlanProvisionRequest;
import in.wynk.subscription.common.message.SubscriptionProvisioningMessage;
import in.wynk.subscription.service.IUserPlanManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SubscriptionProvisioningConsumerPollingQueue extends AbstractSQSMessageConsumerPollingQueue<SubscriptionProvisioningMessage> {

    @Value("${subscription.pooling.queue.provisioning.enabled}")
    private boolean subscriptionNotificationPollingEnabled;
    @Value("${subscription.pooling.queue.provisioning.sqs.consumer.delay}")
    private long subscriptionNotificationPoolingDelay;
    @Value("${subscription.pooling.queue.provisioning.sqs.consumer.delayTimeUnit}")
    private TimeUnit subscriptionNotificationPoolingDelayTimeUnit;

    private final IUserPlanManager planManager;
    private final ISqsManagerService sqsManagerService;
    private final ExecutorService messageHandlerThreadPool;
    private final ScheduledExecutorService pollingThreadPool;

    public SubscriptionProvisioningConsumerPollingQueue(String queueName,
                                                        AmazonSQS sqs,
                                                        ObjectMapper objectMapper,
                                                        IUserPlanManager planManager,
                                                        ISQSMessageExtractor messagesExtractor,
                                                        ISqsManagerService sqsManagerService,
                                                        ExecutorService messageHandlerThreadPool,
                                                        ScheduledExecutorService pollingThreadPool) {
        super(queueName, sqs, objectMapper, messagesExtractor, messageHandlerThreadPool);
        this.planManager = planManager;
        this.sqsManagerService = sqsManagerService;
        this.pollingThreadPool = pollingThreadPool;
        this.messageHandlerThreadPool = messageHandlerThreadPool;
    }

    @Override
    @SneakyThrows
    @AnalyseTransaction(name = BaseConstants.SUBSCRIPTION_PROVISION_QUEUE)
    public void consume(SubscriptionProvisioningMessage message) {
        ProvisioningResponse planProvisioningResponse = null;
        AnalyticService.update(message);
        switch (message.getPaymentEvent()) {
            case RENEW:
            case PURCHASE:
            case SUBSCRIBE:
                planProvisioningResponse = planManager.subscribe(SinglePlanProvisionRequest.builder()
                        .uid(message.getUid())
                        .msisdn(message.getMsisdn())
                        .planId(message.getPlanId())
                        .paymentCode(message.getPaymentCode())
                        .eventType(message.getPaymentEvent())
                        .referenceId(message.getReferenceId())
                        .paymentPartner(message.getPaymentPartner())
                        .status(message.getTransactionStatus())
                        .build());
                break;
            case UNSUBSCRIBE:
                planProvisioningResponse = planManager.unsubscribe(PlanUnProvisioningRequest.builder()
                        .uid(message.getUid())
                        .planId(message.getPlanId())
                        .referenceId(message.getReferenceId())
                        .paymentPartner(message.getPaymentPartner())
                        .build());
                break;
        }

        AnalyticService.update(planProvisioningResponse);

        if (!message.getPaymentPartner().equalsIgnoreCase(BaseConstants.WYNK)) {
            OrderFulfilmentMessageBuilder builder = OrderFulfilmentMessage.builder()
                    .msisdn(message.getMsisdn())
                    .orderId(message.getReferenceId())
                    .partnerId(message.getPaymentPartner())
                    .callbackUrl(message.getCallbackUrl())
                    .planDetails(getObjectMapper().writeValueAsString(planProvisioningResponse));
            if (message.getPaymentEvent() == PaymentEvent.UNSUBSCRIBE) {
                builder.partnerOrder(CancellationOrder.builder().id(message.getReferenceId()).planId(message.getPlanId()).reason("cancelled from payment partner").build());
            } else {
                if (message.getPaymentEvent() == PaymentEvent.PURCHASE) {
                    builder.partnerOrder(FreshOrder.builder().id(message.getReferenceId()).planId(message.getPlanId()).build());
                } else {
                    builder.partnerOrder(RenewOrder.builder().id(message.getReferenceId()).planId(message.getPlanId()).build());
                }
            }
            sqsManagerService.publishSQSMessage(builder.build());
        }
    }

    @Override
    public Class<SubscriptionProvisioningMessage> messageType() {
        return SubscriptionProvisioningMessage.class;
    }

    @Override
    public void start() {
        if (subscriptionNotificationPollingEnabled) {
            log.info("Starting SubscriptionNotificationConsumerPollingQueue...");
            pollingThreadPool.scheduleWithFixedDelay(
                    this::poll,
                    0,
                    subscriptionNotificationPoolingDelay,
                    subscriptionNotificationPoolingDelayTimeUnit
            );
        }
    }

    @Override
    public void stop() {
        if (subscriptionNotificationPollingEnabled) {
            log.info("Shutting down SubscriptionNotificationConsumerPollingQueue ...");
            pollingThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdown();
        }
    }

}