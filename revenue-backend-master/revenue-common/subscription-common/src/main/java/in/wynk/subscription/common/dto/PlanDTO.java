package in.wynk.subscription.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.wynk.subscription.common.enums.PlanType;
import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PlanDTO {

    private int id;
    private String title;
    private String service;
    private int linkedOfferId;
    private int linkedFreePlanId = -1;
    private PriceDTO price;
    private PlanPeriodDTO period;
    private PlanType planType;
    private Map<String, String> sku;

    @JsonIgnore
    public double getFinalPrice() {
        return this.price.getAmount();
    }

    @JsonIgnore
    public long getFinalPriceInPaise() {
        return (long) getFinalPrice() * 100;
    }

    public boolean hasLinkedFreePlan() {
        return linkedFreePlanId != -1;
    }

}
