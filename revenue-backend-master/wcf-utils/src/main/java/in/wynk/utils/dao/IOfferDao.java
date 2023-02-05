package in.wynk.utils.dao;

import in.wynk.utils.domain.OfferConfig;
import in.wynk.utils.domain.OfferConfig.OfferConfigKey;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface IOfferDao extends PagingAndSortingRepository<OfferConfig, OfferConfigKey> {

    List<OfferConfig> findByKey_Service(final String service);

    OfferConfig findByKey_OfferId(final int offerId);


}
 