package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.TransactionStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SinglePlanProvisionRequest extends PlanProvisioningRequest {
    @Analysed
    private Integer planId;
    @Analysed
    @Builder.Default
    private TransactionStatus status = TransactionStatus.SUCCESS;
}
