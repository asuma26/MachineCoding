package in.wynk.order.service.impl;

import in.wynk.cache.aspect.advice.CachePut;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.service.ICacheService;
import in.wynk.order.service.ITransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderDeferredDetailsCacheServiceImpl implements ICacheService<DeferredOrderDetail> {

    private final ITransactionalService<DeferredOrderDetail> deferredOrderTransactionalService;

    public OrderDeferredDetailsCacheServiceImpl(ITransactionalService<DeferredOrderDetail> deferredOrderTransactionalService) {
        this.deferredOrderTransactionalService = deferredOrderTransactionalService;
    }

    @Override
    @CachePut(cacheName = "OrderDeferredDetailsCacheService", cacheKey = "'wynk-order-id:' + #deferredOrderDetail.id", l2CacheTtl = 15 * 60, cacheManager = BeanConstant.L2CACHE_MANAGER)
    public DeferredOrderDetail save(DeferredOrderDetail deferredOrderDetail) {
        return deferredOrderTransactionalService.save(deferredOrderDetail).orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD011));
    }

    @Override
    @Cacheable(cacheName = "OrderDeferredDetailsCacheService", cacheKey = "'wynk-order-id:' + #id", l2CacheTtl = 15 * 60, cacheManager = BeanConstant.L2CACHE_MANAGER)
    public DeferredOrderDetail getByPrimaryId(String id) {
        return deferredOrderTransactionalService.getByPrimaryId(id).orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD013));
    }
}
