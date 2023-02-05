package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.dto.PlanProvisioningRequest;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiPlanProvisionRequest extends PlanProvisioningRequest {

    private WynkService service;

    @Analysed
    private List<Integer> planIds;

    private List<UserPlanDetails> userPlanDetails;

}
