package in.wynk.utils.service.sedb.impl;

import in.wynk.utils.dao.sedb.IOfferDao;
import in.wynk.utils.domain.OfferConfig;
import in.wynk.utils.service.sedb.IOfferService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated
public class OfferServiceImpl implements IOfferService {

    //    @Autowired
    private final IOfferDao offerDao;

    public OfferServiceImpl(IOfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Override
    public OfferConfig save(OfferConfig offer) {
        if (offer != null)
            if (getOfferPeriodicElementById(offer.getKey().getOfferId()) == null)
                return this.offerDao.save(offer);
        return null;
    }

    @Override
    public List<OfferConfig> getOfferPeriodicElements(String service) {
        return offerDao.findByKey_Service(service);
    }

    @Override
    public OfferConfig getOfferPeriodicElementById(int offerId) {
        return offerDao.findByKey_OfferId(offerId);
    }

    @Override
    public boolean switchOfferPeriodicElementById(int offerId, boolean isSystemOffer) {
        OfferConfig offer = this.getOfferPeriodicElementById(offerId);
        offer.setSystemOffer(isSystemOffer);
        this.offerDao.save(offer);
        return true;
    }

    @Override
    public boolean switchOfferPeriodicElements(List<Integer> offerId, boolean isSystemOffer) {
        return offerId.parallelStream().map(_offerId -> this.switchOfferPeriodicElementById(_offerId, isSystemOffer)).reduce((arg1, arg2) -> arg1 && arg2).get();
    }

    @Override
    public OfferConfig update(OfferConfig offer) {
        return this.offerDao.save(offer);
    }

}
