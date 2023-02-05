package in.wynk.order.event;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public abstract class OrderEvent {

    @Analysed
    private final int planId;
    @Analysed
    private final String msisdn;
    @Analysed
    private final String uid;
    @Analysed
    private final String orderId;
    @Analysed
    private final String partnerAlias;
    @Analysed
    private final String callbackUrl;
    @Analysed
    private final PaymentEvent type;

}
