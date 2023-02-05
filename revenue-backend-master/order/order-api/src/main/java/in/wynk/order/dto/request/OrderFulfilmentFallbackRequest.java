package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AnalysedEntity
@SuperBuilder
public class OrderFulfilmentFallbackRequest extends OrderFulfilmentRequest {

    @JsonIgnore
    private final boolean preFulfilled;

    @JsonIgnore
    @JsonProperty("plan_details")
    private final PlanProvisioningResponse planResponse;

}
