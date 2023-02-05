package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.dto.BaseResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AnalysedEntity
public class UserActiveBenefits extends BaseResponse<UserActiveBenefits.UserActivePlans> {
    @Analysed
    private UserActivePlans data;

    @Getter
    @AnalysedEntity
    public static class UserActivePlans {
        @Analysed
        private List<ActiveBenefit> plans;

        public static UserActivePlansBuilder builder() {
            return new UserActivePlansBuilder();
        }

        public static class UserActivePlansBuilder {
            private List<ActiveBenefit> plans;

            public void addPlans(List<ActiveBenefit> benefits) {
                if (CollectionUtils.isNotEmpty(benefits)) {
                    for (ActiveBenefit benefit : benefits) {
                        if (CollectionUtils.isEmpty(plans)) {
                            plans = new ArrayList<>();
                        }
                        plans.add(benefit);
                    }
                }

            }

            public UserActivePlans build() {
                UserActivePlans activePlans = new UserActivePlans();
                if (CollectionUtils.isNotEmpty(plans)) {
                    plans.sort(Comparator.comparingLong(ActiveBenefit::getValidTill));
                }
                activePlans.plans = plans;
                return activePlans;
            }

        }
    }

}
