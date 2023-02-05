package in.wynk.utils.service.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Offer;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOffersService {

    Offer save(Offer offer);

    Offer update(Offer offer);

    void switchState(Integer id, State state);

    Offer find(Integer id);

    List<Offer> findAll(Pageable pageable);

}
