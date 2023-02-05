package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.enums.ProvisionState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanProvisioningResponse extends ProvisioningResponse {

    @Analysed
    private int planId;
    @Analysed
    private long startDate;
    @Analysed
    private long endDate;
    @Analysed
    private boolean autoRenew;
    @Analysed
    private ProvisionState state;

    public boolean isAdditiveValidity() {
        return startDate > System.currentTimeMillis();
    }

}
