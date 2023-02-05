package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.enums.PaymentEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "order_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FreshOrder.class, name = "PURCHASE"),
        @JsonSubTypes.Type(value = RenewOrder.class, name = "SUBSCRIBE"),
        @JsonSubTypes.Type(value = CancellationOrder.class, name = "UNSUBSCRIBE")
})
public abstract class PartnerOrder {

    @Analysed
    @JsonProperty("order_id")
    private String id;
    @Analysed
    @JsonProperty("plan_id")
    private int planId;

    @JsonProperty("order_type")
    @Analysed(name = BaseConstants.PAYMENT_EVENT)
    public abstract PaymentEvent getType();

}
