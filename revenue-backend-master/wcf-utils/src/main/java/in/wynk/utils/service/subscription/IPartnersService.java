package in.wynk.utils.service.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Partner;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPartnersService {

    Partner save(Partner partner);

    Partner update(Partner partner);

    void switchState(String id, State state);

    Partner find(String id);

    List<Partner> findAll(Pageable pageable);

}
