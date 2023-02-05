package in.wynk.subscription.dto;

import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class OfferPlanProductsForProvision {

    private final Set<Offer> offers;
    private final Set<Plan> plans;
    private final Set<Product> products;
    private final Map<Plan, Set<Product>> planProductMap;
    private final Map<Plan, Set<Product>> activePaidPlanProductMap;

}
