package in.wynk.utils.dao.sedb;

import in.wynk.utils.dao.IBaseDao;
import in.wynk.utils.domain.SubscriptionPack;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface ISubscriptionPackDao extends IBaseDao<SubscriptionPack, Integer> {

    List<SubscriptionPack> findByService(String service);

    SubscriptionPack findByProductId(final int productId);
}
