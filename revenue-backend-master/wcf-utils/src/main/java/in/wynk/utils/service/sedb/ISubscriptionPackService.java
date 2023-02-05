package in.wynk.utils.service.sedb;

import in.wynk.utils.domain.SubscriptionPack;
import in.wynk.utils.dto.PackPeriodicElement;

import java.util.List;

@Deprecated
public interface ISubscriptionPackService {

    SubscriptionPack save(SubscriptionPack pack);

    SubscriptionPack update(SubscriptionPack pack);

    SubscriptionPack getSubscriptionPackById(int productId);

    List<PackPeriodicElement> getPackPeriodicElements(String service);

    boolean switchPackPeriodicElementById(final int packId, boolean isDeprecated);

    boolean switchPackPeriodicElements(final List<Integer> packIds, boolean isDeprecated);

}
