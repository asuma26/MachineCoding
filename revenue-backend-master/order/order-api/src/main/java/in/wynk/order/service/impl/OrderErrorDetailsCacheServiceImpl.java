package in.wynk.order.service.impl;

import in.wynk.order.core.dao.entity.OrderErrorDetails;
import in.wynk.order.core.dao.repository.IOrderErrorDao;
import in.wynk.order.service.ICacheService;
import org.springframework.stereotype.Service;

@Service
public class OrderErrorDetailsCacheServiceImpl implements ICacheService<OrderErrorDetails> {

    private final IOrderErrorDao orderErrorDao;

    public OrderErrorDetailsCacheServiceImpl(IOrderErrorDao orderErrorDao) {
        this.orderErrorDao = orderErrorDao;
    }

    @Override
    public OrderErrorDetails save(OrderErrorDetails orderErrorDetails) {
        return orderErrorDao.save(orderErrorDetails);
    }

    @Override
    public OrderErrorDetails getByPrimaryId(String orderId) {
        return orderErrorDao.findById(orderId).get();
    }

}
