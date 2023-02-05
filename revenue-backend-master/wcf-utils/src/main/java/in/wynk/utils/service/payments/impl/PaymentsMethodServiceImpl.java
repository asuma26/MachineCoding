package in.wynk.utils.service.payments.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.payment.core.dao.entity.PaymentMethod;
import in.wynk.payment.core.dao.repository.PaymentMethodDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.payments.IPaymentsMethodService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentsMethodServiceImpl implements IPaymentsMethodService {

    private final PaymentMethodDao paymentsMethodDao;

    public PaymentsMethodServiceImpl(PaymentMethodDao paymentsMethodDao) {
        this.paymentsMethodDao = paymentsMethodDao;
    }

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return paymentsMethodDao.save(paymentMethod);
    }

    @Override
    public PaymentMethod update(PaymentMethod paymentMethod) {
        PaymentMethod paymentMethod1 = find(paymentMethod.getId());
        return save(paymentMethod);
    }

    @Override
    public void switchState(String id, State state) {
        PaymentMethod paymentMethod = find(id);
        paymentMethod.setState(state);
        save(paymentMethod);
    }

    @Override
    public PaymentMethod find(String id) {
        return paymentsMethodDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF013));
    }

    @Override
    public List<PaymentMethod> findAll(Pageable pageable) {
        return paymentsMethodDao.findAll(pageable).getContent();
    }
}
