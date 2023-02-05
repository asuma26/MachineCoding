package in.wynk.order.service;

import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.dto.request.OrderDeferRequest;

import java.util.stream.Stream;

public interface IDeferredService {

    void deferred(OrderDeferRequest request);

    DeferredOrderDetail getDeferredOrderDetails(String orderId);

    Stream<DeferredOrderDetail> getScheduledDeferredOrdersPaginated();

}
