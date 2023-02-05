package in.wynk.order.service;

import in.wynk.order.dto.response.OrderStatusResponse;

public interface IStatusService {

    OrderStatusResponse status(String orderId, String msisdn);

}
