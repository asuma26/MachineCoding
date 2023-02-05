package in.wynk.order.service.impl;

import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.core.dao.repository.IOrderDao;
import in.wynk.order.service.ITransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class OrderTransactionalServiceImpl implements ITransactionalService<Order> {

    private final IOrderDao orderDao;

    public OrderTransactionalServiceImpl(IOrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 2)
    public Optional<Order> save(Order order) {
        return Optional.of(orderDao.save(order));
    }

    @Override
    @Transactional(readOnly = true, timeout = 2)
    public Optional<Order> getByPrimaryId(String wynkOrderId) {
        return orderDao.findById(wynkOrderId);
    }

    @Override
    @Transactional(readOnly = true, timeout = 2)
    public Optional<Order> getBySecondaryId(String partnerOrderId) {
        return orderDao.findByPartnerOrderId(partnerOrderId);
    }
}
