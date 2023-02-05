package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.dto.BaseResponse;
import in.wynk.subscription.dto.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSubscriptionStatusResponse extends BaseResponse<UserSubscriptionStatusResponse.SubscriptionStatusData> {
    @Analysed
    private SubscriptionStatusData data;

    @Builder
    @AnalysedEntity
    @Getter
    public static class SubscriptionStatusData {
        @Analysed
        List<SubscriptionStatus> status;
    }
}
