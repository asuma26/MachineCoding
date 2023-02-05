package in.wynk.subscription.dto.response;

import in.wynk.subscription.core.dao.entity.Product;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Getter
@SuperBuilder
public class ActiveProductsComputationResponse extends ProductsComputationResponse {
    private final Map<Integer, Set<Product>> activePlanProductMap;
}
