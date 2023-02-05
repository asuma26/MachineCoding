package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class PartnerOrderDetails {

    @Analysed
    private final String currency;
    @Analysed
    private final String customerId;
    @Analysed
    private final String promoCode;
    @Analysed
    private final double amount;
    @Analysed
    private final double cashbackOffered;
    @Analysed
    private final double discountOffered;

    @JsonCreator
    public PartnerOrderDetails(@JsonProperty("currency") String currency, @JsonProperty("customer_id") String customerId, @JsonProperty("promo_code") String promoCode, @JsonProperty("amount") double amount, @JsonProperty("cashback_offered") double cashbackOffered, @JsonProperty("discount_offered") double discountOffered) {
        this.currency = currency;
        this.customerId = customerId;
        this.promoCode = promoCode;
        this.amount = amount;
        this.cashbackOffered = cashbackOffered;
        this.discountOffered = discountOffered;
    }
}
