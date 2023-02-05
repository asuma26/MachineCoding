package in.wynk.subscription.dto.response;

import in.wynk.subscription.core.dao.entity.Product;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Getter
@SuperBuilder
public class EligibleProductsComputationResponse extends ProductsComputationResponse {
    private final Map<Integer, Set<Product>> eligiblePlanProductMap;
    private final Map<Integer, Set<Product>> combinedPlanProductMap;
}
