package in.wynk.utils.service.sedb;

import in.wynk.utils.domain.OfferConfig;

import java.util.List;

@Deprecated
public interface IOfferService {

    OfferConfig save(OfferConfig offer);

    List<OfferConfig> getOfferPeriodicElements(final String service);

    OfferConfig getOfferPeriodicElementById(final int offerId);

    boolean switchOfferPeriodicElementById(final int offerId, boolean isSystemOffer);

    boolean switchOfferPeriodicElements(final List<Integer> offerId, boolean isSystemOffer);

    OfferConfig update(OfferConfig offer);

}
