package in.wynk.order.event;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.enums.OrderStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class ProvisionalOrderFulfilledEvent extends OrderEvent {
    @Analysed
    private final OrderStatus status;
    @Analysed
    private final String paymentCode;
}
