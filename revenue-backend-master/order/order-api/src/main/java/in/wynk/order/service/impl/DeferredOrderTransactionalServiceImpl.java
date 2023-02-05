package in.wynk.order.service.impl;

import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.core.dao.repository.IDeferredOrderDetailsDao;
import in.wynk.order.service.ITransactionalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeferredOrderTransactionalServiceImpl implements ITransactionalService<DeferredOrderDetail> {

    private final IDeferredOrderDetailsDao deferredOrderDetailsDao;

    public DeferredOrderTransactionalServiceImpl(IDeferredOrderDetailsDao deferredOrderDetailsDao) {
        this.deferredOrderDetailsDao = deferredOrderDetailsDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<DeferredOrderDetail> save(DeferredOrderDetail deferredOrderDetail) {
        return Optional.of(deferredOrderDetailsDao.save(deferredOrderDetail));
    }

    @Override
    public Optional<DeferredOrderDetail> getByPrimaryId(String orderId) {
        return deferredOrderDetailsDao.findById(orderId);
    }
}
