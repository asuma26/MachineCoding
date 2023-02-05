package in.wynk.order.event;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class OrderNotifiedEvent extends OrderEvent {
}
