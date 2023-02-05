package in.wynk.subscription.service;

import in.wynk.subscription.core.dto.NotificationDetails;

/**
 * @author Abhishek
 * @created 15/07/20
 */
public interface INotificationService {
    void sendNotificationToUser(NotificationDetails notificationDetails);
}
