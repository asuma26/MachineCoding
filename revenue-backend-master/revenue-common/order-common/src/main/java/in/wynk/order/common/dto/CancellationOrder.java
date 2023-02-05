package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CancellationOrder extends PartnerOrder {

    @Analysed
    @JsonProperty("cancellation_reason")
    private String reason;

    @Override
    public PaymentEvent getType() {
        return PaymentEvent.UNSUBSCRIBE;
    }
}
