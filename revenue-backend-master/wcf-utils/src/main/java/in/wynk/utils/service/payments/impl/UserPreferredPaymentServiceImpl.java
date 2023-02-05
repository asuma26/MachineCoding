package in.wynk.utils.service.payments.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.payment.core.dao.entity.UserPreferredPayment;
import in.wynk.payment.core.dao.repository.UserPreferredPaymentsDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.payments.IUserPreferredPaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferredPaymentServiceImpl implements IUserPreferredPaymentService {

    private final UserPreferredPaymentsDao userPreferredPaymentsDao;

    public UserPreferredPaymentServiceImpl(UserPreferredPaymentsDao userPreferredPaymentDao) {
        this.userPreferredPaymentsDao = userPreferredPaymentDao;
    }

    @Override
    public UserPreferredPayment save(UserPreferredPayment userPreferredPayment) {
        return userPreferredPaymentsDao.save(userPreferredPayment);
    }

    @Override
    public UserPreferredPayment update(UserPreferredPayment userPreferredPayment) {
        UserPreferredPayment userPreferredPayment1 = find(userPreferredPayment.getId());
        return save(userPreferredPayment);
    }

    @Override
    public void switchState(String id, State state) {
        UserPreferredPayment userPreferredPayment = find(id);
        userPreferredPayment.setState(state);
        save(userPreferredPayment);
    }

    @Override
    public UserPreferredPayment find(String id) {
        return userPreferredPaymentsDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF014));
    }

    @Override
    public List<UserPreferredPayment> findAll(Pageable pageable) {
        return userPreferredPaymentsDao.findAll(pageable).getContent();
    }
}
