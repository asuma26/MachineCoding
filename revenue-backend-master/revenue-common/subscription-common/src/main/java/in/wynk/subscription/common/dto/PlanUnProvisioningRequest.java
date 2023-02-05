package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
public class PlanUnProvisioningRequest {

    @Analysed
    private Integer planId;
    @Analysed
    private String uid;
    @Analysed
    private String referenceId;
    @Analysed
    private String paymentPartner;

}
