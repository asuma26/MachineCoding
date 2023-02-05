package in.wynk.utils.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

import java.util.List;

@Getter
@AnalysedEntity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
public class SubscriptionAndUserPlanResponse {
    @Analysed
    private List<UserPlanDetailResponse> activePlanDetail;

    @Analysed
    private List<SubscriptionProductResponse> activeSubscriptions;
}
