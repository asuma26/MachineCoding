package in.wynk.subscription.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.payment.common.messages.PaymentRecurringSchedulingMessage;
import in.wynk.queue.constant.QueueConstant;
import in.wynk.queue.dto.MessageThresholdExceedEvent;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.core.constants.BeanConstant;
import in.wynk.subscription.core.dto.ClientEventDetails;
import in.wynk.subscription.core.dto.NotificationDetails;
import in.wynk.subscription.core.events.RenewalMigrationEvent;
import in.wynk.subscription.core.events.SubscriptionEvent;
import in.wynk.subscription.event.NotificationEvent;
import in.wynk.subscription.service.INonRevenueService;
import in.wynk.subscription.service.INotificationService;
import in.wynk.subscription.service.PaymentService;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static in.wynk.queue.constant.BeanConstant.MESSAGE_PAYLOAD;

@Service
@Slf4j
public class SubscriptionEventsListener {

    @Value("${spring.application.name}")
    private String applicationAlias;
    private final ObjectMapper mapper;
    private final ApplicationContext context;
    private final RetryRegistry retryRegistry;
    private final ISqsManagerService sqsManagerService;

    public SubscriptionEventsListener(ApplicationContext context, @Qualifier(in.wynk.queue.constant.BeanConstant.RESILIENCE_RETRY) RetryRegistry retryRegistry, ISqsManagerService sqsManagerService, ObjectMapper mapper) {
        this.mapper = mapper;
        this.context = context;
        this.retryRegistry = retryRegistry;
        this.sqsManagerService = sqsManagerService;
    }

    @EventListener
    @AnalyseTransaction(name = "subscriptionEvent")
    public void onSubscriptionProvisioningEvent(SubscriptionEvent event) {
        AnalyticService.update(event);
        switch (event.getEvent()) {
            case UNSUBSCRIBE:
            case DEFERRED:
                this.unScheduleRecurring(event);
                break;
            case SUBSCRIBE:
                break;
        }
        this.publishToExternalClient(event);
    }

    private void unScheduleRecurring(SubscriptionEvent event) {
        if (event.getReferenceId() != null) {
            context.getBean(BeanConstant.REVENUE_PAYMENT_SERVICE, PaymentService.class).unScheduleUserPaymentRecurrence(event.getPlanId(), event.getUid(), event.getReferenceId(), event.getValidTillDate(), event.getDeferredUntil(), event.getEvent());
        }
    }

    private void publishToExternalClient(SubscriptionEvent event) {
        ClientEventDetails clientEventDetails = ClientEventDetails.builder()
                .uid(event.getUid())
                .msisdn(event.getMsisdn())
                .planId(event.getPlanId())
                .event(event.getEvent().name())
                .autoRenewal(event.isAutoRenewal())
                .validTillDate(event.getValidTillDate())
                .service(event.getService().getValue())
                .build();
        switch (event.getService()) {
            case MUSIC:
                retryRegistry.retry(BeanConstant.WYNK_MUSIC_S2S_CLIENT).executeRunnable(() -> context.getBean(BeanConstant.WYNK_MUSIC_S2S_CLIENT, INonRevenueService.class).publishEvent(clientEventDetails));
                break;
            case AIRTEL_TV:
                retryRegistry.retry(BeanConstant.AIRTEL_XSTREAM_S2S_CLIENT).executeRunnable(() -> context.getBean(BeanConstant.AIRTEL_XSTREAM_S2S_CLIENT, INonRevenueService.class).publishEvent(clientEventDetails));
                break;
            case BOOKS:
                break;
        }
        retryRegistry.retry(BeanConstant.UT_S2S_CLIENT).executeRunnable(() -> context.getBean(BeanConstant.UT_S2S_CLIENT, INonRevenueService.class).publishEvent(clientEventDetails));
    }

    @EventListener
    @AnalyseTransaction(name = "notificationEvent")
    public void notifyUser(NotificationEvent event) {
        AnalyticService.update(event);
        NotificationDetails notificationDetails = NotificationDetails.builder()
                .priority(event.getPriority())
                .service(event.getService())
                .message(event.getMessage())
                .msisdn(event.getMsisdn())
                .build();
        context.getBean(INotificationService.class).sendNotificationToUser(notificationDetails);
    }

    @EventListener
    @AnalyseTransaction(name = "renewalMigrationEvent")
    public void renewalEvent(RenewalMigrationEvent event) {
        AnalyticService.update(event);
        sqsManagerService.publishSQSMessage(PaymentRecurringSchedulingMessage.builder()
                .uid(event.getUid())
                .clientAlias(applicationAlias)
                .event(PaymentEvent.SUBSCRIBE)
                .paymentCode(event.getPaymentCode())
                .paymentMetaData(event.getPaymentMetaData())
                .nextChargingDate(event.getNextChargingDate())
                .msisdn(event.getPaymentMetaData().get(BaseConstants.MSISDN))
                .planId(Integer.parseInt(event.getPaymentMetaData().get(BaseConstants.PLAN_ID)))
                .build());
    }

    @EventListener
    @AnalyseTransaction(name = QueueConstant.DEFAULT_SQS_MESSAGE_THRESHOLD_EXCEED_EVENT)
    public void onAnyOrderMessageThresholdExceedEvent(MessageThresholdExceedEvent event) throws JsonProcessingException {
        AnalyticService.update(event);
        AnalyticService.update(MESSAGE_PAYLOAD, mapper.writeValueAsString(event));
    }

}
