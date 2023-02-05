package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OrderPlacementFallbackRequest extends OrderPlacementRequest {
    @Analysed
    @JsonProperty("fallback_order_id")
    @JsonIgnore
    private final String fallbackOrderId;
}
