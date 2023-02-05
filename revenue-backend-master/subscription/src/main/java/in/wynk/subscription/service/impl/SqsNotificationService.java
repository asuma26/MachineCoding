package in.wynk.subscription.service.impl;

import in.wynk.queue.service.ISqsManagerService;
import in.wynk.sms.common.message.SmsNotificationMessage;
import in.wynk.subscription.core.dto.NotificationDetails;
import in.wynk.subscription.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Abhishek
 * @created 15/07/20
 */
@Service
@Slf4j
public class SqsNotificationService implements INotificationService {

    private final ISqsManagerService sqsManagerService;

    public SqsNotificationService(ISqsManagerService sqsManagerService) {
        this.sqsManagerService = sqsManagerService;
    }


    @Override
    public void sendNotificationToUser(NotificationDetails notificationDetails) {
        try {
            log.debug("notificationDetails: {}", notificationDetails);
            SmsNotificationMessage notificationMessage = SmsNotificationMessage.builder()
                    .message(notificationDetails.getMessage())
                    .msisdn(notificationDetails.getMsisdn())
                    .priority(notificationDetails.getPriority())
                    .service(notificationDetails.getService())
                    .build();
            sqsManagerService.publishSQSMessage(notificationMessage);
        } catch (Exception e) {
            log.error("Unable to send message to notification queue: {}", notificationDetails);
        }
    }
}
