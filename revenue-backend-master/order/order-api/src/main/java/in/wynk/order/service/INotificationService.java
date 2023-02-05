package in.wynk.order.service;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.dto.request.OrderNotificationRequest;
import in.wynk.order.dto.response.OrderNotificationResponse;

public interface INotificationService {

    default OrderNotificationResponse notify(OrderNotificationRequest request) {
        switch (request.getExecutionType()) {
            case SYNC:
                return buildNotificationResponse(request);
            case ASYNC:
                return notifyAsync(request);
            default:
                throw new WynkRuntimeException("unknown execution type");
        }
    }

    OrderNotificationResponse buildNotificationResponse(OrderNotificationRequest request);

    OrderNotificationResponse notifyAsync(OrderNotificationRequest request);

}
