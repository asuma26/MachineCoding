package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import in.wynk.common.enums.PaymentEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreshOrder extends PartnerOrder {

    @Analysed
    @JsonProperty("order_details")
    private PartnerOrderDetails orderDetails;
    @Analysed
    @JsonProperty("payment_details")
    private PartnerPaymentDetails paymentDetails;

    @Override
    public PaymentEvent getType() {
        return PaymentEvent.PURCHASE;
    }

}
