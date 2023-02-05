package in.wynk.order.dto.request;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class OrderDeferredFulfilRequest {

    @Analysed
    private final String orderId;
    @Analysed
    private final boolean preFulfilled;
    @Analysed
    private final long deferredUntil;
    @Analysed
    private final long validUntil;
    @Analysed
    private final String callbackUrl;

}
