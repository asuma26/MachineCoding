package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.dto.ProvisioningResponse;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiPlanProvisioningResponse extends ProvisioningResponse {
    private Map<UserPlanDetails, Set<Product>> provisionedPlanProductsDetails;
}
