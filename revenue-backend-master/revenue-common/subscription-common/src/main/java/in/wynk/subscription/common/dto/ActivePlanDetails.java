package in.wynk.subscription.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivePlanDetails {
    private int planId;
    private boolean autoRenew;
    private Date validFromDate;
    private Date validTillDate;
}
