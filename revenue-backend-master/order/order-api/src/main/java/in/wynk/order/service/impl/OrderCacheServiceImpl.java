package in.wynk.order.service.impl;

import in.wynk.cache.aspect.advice.CachePut;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.service.ICacheService;
import in.wynk.order.service.ITransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderCacheServiceImpl implements ICacheService<Order> {

    private final ITransactionalService<Order> orderTransactionalService;

    public OrderCacheServiceImpl(ITransactionalService<Order> orderTransactionalService) {
        this.orderTransactionalService = orderTransactionalService;
    }

    @Override
    @CachePut(cacheName = "OrderCacheService", cacheKey = "'wynk-order-id:' + #order.id", l1CacheTtl = 2 * 60, l2CacheTtl = 15 * 60)
    public Order save(Order order) {
        return orderTransactionalService.save(order).orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD011));
    }

    @Override
    @Cacheable(cacheName = "OrderCacheService", cacheKey = "'wynk-order-id:' + #orderId", l1CacheTtl = 2 * 60, l2CacheTtl = 15 * 60)
    public Order getByPrimaryId(String orderId) {
        return orderTransactionalService.getByPrimaryId(orderId).orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD006));
    }

    @Override
    @Cacheable(cacheName = "OrderCacheService", cacheKey = "'partner-id:' + #partnerOrderId", l2CacheTtl = 15 * 60, cacheManager = BeanConstant.L2CACHE_MANAGER)
    public Order getBySecondaryId(String partnerOrderId) {
        return orderTransactionalService.getBySecondaryId(partnerOrderId).orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
    }
}
