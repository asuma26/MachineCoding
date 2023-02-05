package in.wynk.order.event;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.enums.OrderStatusDetail;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderErrorEvent extends OrderEvent {
    @Analysed
    private final OrderStatusDetail statusDetail;
}
