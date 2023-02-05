package in.wynk.order.dto.request;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

@Getter
@Builder
@AnalysedEntity
public class OrderDeferRequest {

    @Analysed
    private final boolean preFulfilled;
    @Analysed
    private final String orderId;
    @Analysed
    private final String callbackUrl;
    @Analysed
    @Temporal(TemporalType.TIMESTAMP)
    private final Calendar untilDate;
    @Analysed
    @Temporal(TemporalType.TIMESTAMP)
    private final Calendar validUntil;
    @Analysed
    @Builder.Default
    private final OrderStatus orderStatus = OrderStatus.DEFERRED;

}
