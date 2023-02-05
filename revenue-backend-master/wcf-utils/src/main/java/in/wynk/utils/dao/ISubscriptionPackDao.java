package in.wynk.utils.dao;

import in.wynk.utils.domain.SubscriptionPack;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface ISubscriptionPackDao extends PagingAndSortingRepository<SubscriptionPack, Integer> {

    List<SubscriptionPack> findByService(String service);

    SubscriptionPack findByProductId(final int productId);
}
