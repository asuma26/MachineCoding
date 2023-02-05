package in.wynk.subscription.service;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.enums.TransactionStatus;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.payment.common.messages.PaymentRecurringUnSchedulingMessage;
import in.wynk.queue.constant.QueueLoggingMarker;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.core.constants.BeanConstant;
import in.wynk.subscription.core.constants.SubscriptionErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service(BeanConstant.REVENUE_PAYMENT_SERVICE)
public class PaymentService {

    @Value("service.payment.api.endpoints.transactionStatus")
    private String transactionStatusEndpoint;
    private final RestTemplate paymentServiceTemplate;
    private final ISqsManagerService sqsManagerService;

    public PaymentService(@Qualifier("internalServiceTemplate") RestTemplate paymentServiceTemplate, ISqsManagerService sqsManagerService) {
        this.paymentServiceTemplate = paymentServiceTemplate;
        this.sqsManagerService = sqsManagerService;
    }

    public void unScheduleUserPaymentRecurrence(int planId, String uid, String transactionId, long validTill, long deferredUntil, PaymentEvent paymentEvent) {
        try {
            sqsManagerService.publishSQSMessage(PaymentRecurringUnSchedulingMessage.builder()
                    .uid(uid)
                    .planId(planId)
                    .validUntil(validTill)
                    .paymentEvent(paymentEvent)
                    .transactionId(transactionId)
                    .deferredUntil(deferredUntil)
                    .build());
        } catch (Exception e) {
            log.error(QueueLoggingMarker.SQS_ERROR,
                    "Unable to publish PaymentRecurringUnSchedulingMessage for uid: {}, planId: {} and transactionId: {}",
                    uid, planId, transactionId);
        }
    }

    public TransactionStatus fetchTransactionStatus(String sid) {
        ResponseEntity<TransactionStatus> responseEntity = paymentServiceTemplate.exchange(transactionStatusEndpoint + sid, HttpMethod.GET, null, TransactionStatus.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (responseEntity.getBody() != TransactionStatus.SUCCESS) {
                throw new WynkRuntimeException(SubscriptionErrorType.SUB001);
            } else {
                return responseEntity.getBody();
            }
        } else {
            throw new WynkRuntimeException(SubscriptionErrorType.SUB002);
        }
    }

}
