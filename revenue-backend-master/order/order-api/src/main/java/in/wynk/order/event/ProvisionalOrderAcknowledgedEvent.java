package in.wynk.order.event;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.PartnerOrder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class ProvisionalOrderAcknowledgedEvent extends OrderEvent {
    @Analysed
    private final PartnerOrder partnerOrder;
}
