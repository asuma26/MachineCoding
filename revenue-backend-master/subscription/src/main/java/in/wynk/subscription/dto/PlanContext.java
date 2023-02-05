package in.wynk.subscription.dto;

import in.wynk.common.enums.AppId;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlanContext {

    private int planId;
    private AppId appId;
    private List<UserPlanDetails> userPlanDetails;

}