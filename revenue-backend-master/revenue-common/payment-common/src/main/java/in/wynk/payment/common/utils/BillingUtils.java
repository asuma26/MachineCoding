package in.wynk.payment.common.utils;

import in.wynk.payment.common.enums.BillingCycle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BillingUtils {

    private int billingInterval;
    private BillingCycle billingCycle;

    public BillingUtils(int validTillDays) {
        if (validTillDays % 365 == 0) {
            this.billingCycle = BillingCycle.YEARLY;
            this.billingInterval = validTillDays / 365;
        } else if (validTillDays % 30 == 0) {
            this.billingCycle = BillingCycle.MONTHLY;
            this.billingInterval = validTillDays / 30;
        } else if (validTillDays % 7 == 0) {
            this.billingCycle = BillingCycle.WEEKLY;
            this.billingInterval = validTillDays / 7;
        } else {
            this.billingCycle = BillingCycle.DAILY;
            this.billingInterval = validTillDays;
        }
    }

}
