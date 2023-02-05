
package in.wynk.subscription.core.dao.entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Price {

    private boolean bestPlan;
    private int amount;
    private String cur;
    private int monthlyAmount;
    private String savings;
    private int displayAmount;
}
