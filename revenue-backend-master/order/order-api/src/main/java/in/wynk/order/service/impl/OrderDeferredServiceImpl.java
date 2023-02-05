package in.wynk.order.service.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.core.dao.repository.IDeferredOrderDetailsDao;
import in.wynk.order.dto.request.OrderDeferRequest;
import in.wynk.order.service.ICacheService;
import in.wynk.order.service.IDeferredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderDeferredServiceImpl implements IDeferredService {

    private final int deferredOffsetDay;
    private final IDeferredOrderDetailsDao deferredOrderDetailsDao;
    private final ICacheService<DeferredOrderDetail> deferredOrderDetailCacheService;

    public OrderDeferredServiceImpl(@Value("${deferred.orders.offset.day}") int deferredOffsetDay, IDeferredOrderDetailsDao deferredOrderDetailsDao, ICacheService<DeferredOrderDetail> deferredOrderDetailCacheService) {
        this.deferredOffsetDay = deferredOffsetDay;
        this.deferredOrderDetailsDao = deferredOrderDetailsDao;
        this.deferredOrderDetailCacheService = deferredOrderDetailCacheService;
    }


    @Override
    public void deferred(OrderDeferRequest request) {
        try {
            if (request.getOrderStatus() == OrderStatus.FULFILLED) {
                DeferredOrderDetail deferredOrderDetail = deferredOrderDetailCacheService.getByPrimaryId(request.getOrderId());
                deferredOrderDetail.setStatus(request.getOrderStatus().getStatus());
                deferredOrderDetail.setUpdatedOn(Calendar.getInstance());
                deferredOrderDetail.persisted();
                deferredOrderDetailCacheService.save(deferredOrderDetail);
            } else {
                deferredOrderDetailCacheService.save(DeferredOrderDetail.builder()
                        .id(request.getOrderId())
                        .preFulfilled(request.isPreFulfilled())
                        .callbackUrl(request.getCallbackUrl())
                        .status(OrderStatus.DEFERRED.name())
                        .validUntil(request.getValidUntil())
                        .untilDate(request.getUntilDate())
                        .createdOn(Calendar.getInstance())
                        .build());
            }
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD005, e);
        }
    }

    @Override
    public DeferredOrderDetail getDeferredOrderDetails(String orderId) {
        return deferredOrderDetailCacheService.getByPrimaryId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<DeferredOrderDetail> getScheduledDeferredOrdersPaginated() {
        Calendar currentDay = Calendar.getInstance();
        Calendar currentDayWithOffset = Calendar.getInstance();
        currentDayWithOffset.add(Calendar.DAY_OF_MONTH, deferredOffsetDay);
        return deferredOrderDetailsDao.getScheduledDeferredOrdersPaginated(currentDay, currentDayWithOffset);
    }

}
