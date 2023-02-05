package in.wynk.subscription.dto.request;

import in.wynk.subscription.core.dao.entity.Plan;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
public abstract class ProductsComputationRequest {
    private final Set<Plan> activePlans;
}
