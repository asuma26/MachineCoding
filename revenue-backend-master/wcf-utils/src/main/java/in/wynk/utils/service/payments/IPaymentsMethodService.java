package in.wynk.utils.service.payments;

import in.wynk.data.enums.State;
import in.wynk.payment.core.dao.entity.PaymentMethod;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPaymentsMethodService {

    PaymentMethod save(PaymentMethod paymentMethod);

    PaymentMethod update(PaymentMethod paymentMethod);

    void switchState(String id, State state);

    PaymentMethod find(String id);

    List<PaymentMethod> findAll(Pageable pageable);

}
