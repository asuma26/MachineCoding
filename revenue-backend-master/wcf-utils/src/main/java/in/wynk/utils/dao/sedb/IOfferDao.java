package in.wynk.utils.dao.sedb;

import in.wynk.utils.dao.IBaseDao;
import in.wynk.utils.domain.OfferConfig;
import in.wynk.utils.domain.OfferConfig.OfferConfigKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface IOfferDao extends IBaseDao<OfferConfig, OfferConfigKey> {

    List<OfferConfig> findByKey_Service(final String service);

    OfferConfig findByKey_OfferId(final int offerId);


}
 