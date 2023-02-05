package in.wynk.utils.service.subscription.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.repository.subscription.OfferDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.subscription.IOffersService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OffersServiceImpl implements IOffersService {

    private final OfferDao offersDao;

    public OffersServiceImpl(OfferDao offersDao) {
        this.offersDao = offersDao;
    }

    @Override
    public Offer save(Offer offer) {
        return offersDao.save(offer);
    }

    @Override
    public Offer update(Offer offer) {
        Offer offer1 = find(offer.getId());
        return save(offer);
    }

    @Override
    public void switchState(Integer id, State state) {
        Offer offer = find(id);
        offer.setState(state);
        save(offer);
    }

    @Override
    public Offer find(Integer id) {
        return offersDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF015));
    }

    @Override
    public List<Offer> findAll(Pageable pageable) {
        return offersDao.findAll(pageable).getContent();
    }

}
