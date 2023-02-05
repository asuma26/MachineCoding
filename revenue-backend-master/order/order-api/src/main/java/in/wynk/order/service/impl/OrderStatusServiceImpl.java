package in.wynk.order.service.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.dto.WynkOrderDetails;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.core.dao.entity.OrderErrorDetails;
import in.wynk.order.dto.response.OrderStatusResponse;
import in.wynk.order.service.ICacheService;
import in.wynk.order.service.IStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class OrderStatusServiceImpl implements IStatusService {

    private final ICacheService<Order> orderCahceService;
    private final ICacheService<OrderErrorDetails> orderErrorDetailsCacheService;

    public OrderStatusServiceImpl(ICacheService<Order> orderCahceService, ICacheService<OrderErrorDetails> orderErrorDetailsCacheService) {
        this.orderCahceService = orderCahceService;
        this.orderErrorDetailsCacheService = orderErrorDetailsCacheService;
    }

    @Override
    public OrderStatusResponse status(String orderId, String msisdn) {
        try {
            Optional<Order> option = Optional.ofNullable(orderCahceService.getByPrimaryId(orderId));
            if (option.isPresent()) {
                Order order = option.get();
                OrderStatusDetail statusDetail;
                switch (order.getStatus()) {
                    case FULFILLED:
                        statusDetail = OrderStatusDetail.ORDER_SUC203;
                        break;
                    case DEFERRED:
                        statusDetail = OrderStatusDetail.ORDER_SUC205;
                        break;
                    case ACKNOWLEDGED:
                        statusDetail = OrderStatusDetail.ORDER_SUC201;
                        break;
                    case FAILED:
                        statusDetail = OrderStatusDetail.ORDER_FAIL403;
                        break;
                    default:
                        statusDetail = OrderStatusDetail.ORDER_FAIL404;
                }
                return OrderStatusResponse.builder().orderId(orderId).msisdn(msisdn).orderDetails(WynkOrderDetails.builder().type(order.getType()).status(order.getStatus()).statusDetail(statusDetail).build()).build();
            } else {
                OrderErrorDetails orderErrorDetails = orderErrorDetailsCacheService.getByPrimaryId(orderId);
                return OrderStatusResponse.builder().orderId(orderId).msisdn(msisdn).orderDetails(WynkOrderDetails.builder().type(orderErrorDetails.getType()).status(OrderStatus.FAILED).statusDetail(orderErrorDetails.getStatusDetails()).build()).build();
            }
        } catch (Exception e) {
            throw new WynkRuntimeException(e.getMessage(), e);
        }
    }

}
