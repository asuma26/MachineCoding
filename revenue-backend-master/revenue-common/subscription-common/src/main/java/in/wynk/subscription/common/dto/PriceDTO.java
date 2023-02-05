package in.wynk.subscription.common.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PriceDTO {

    private double amount;
    private boolean bestPlan;

    private String currency;
    private String savings;

    private int monthlyAmount;
    private int displayAmount;

}
