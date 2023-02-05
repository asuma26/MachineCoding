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
public class RenewOrder extends PartnerOrder {

    @Analysed
    @JsonProperty("order_details")
    private PartnerOrderDetails orderDetails;
    @Analysed
    @JsonProperty("payment_details")
    private PartnerPaymentDetails paymentDetails;

    @Override
    public PaymentEvent getType() {
        return PaymentEvent.SUBSCRIBE;
    }

}
