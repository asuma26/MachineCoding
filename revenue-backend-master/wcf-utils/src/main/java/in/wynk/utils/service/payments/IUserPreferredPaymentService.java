package in.wynk.utils.service.payments;

import in.wynk.data.enums.State;
import in.wynk.payment.core.dao.entity.UserPreferredPayment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserPreferredPaymentService {

    UserPreferredPayment save(UserPreferredPayment userPreferredPayment);

    UserPreferredPayment update(UserPreferredPayment userPreferredPayment);

    void switchState(String id, State state);

    UserPreferredPayment find(String id);

    List<UserPreferredPayment> findAll(Pageable pageable);


}
