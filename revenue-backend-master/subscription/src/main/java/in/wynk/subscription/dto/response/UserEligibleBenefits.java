package in.wynk.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.dto.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Getter
@SuperBuilder
@AnalysedEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEligibleBenefits extends BaseResponse<UserEligibleBenefits.UserEligibleBenefitsData> {

    @Analysed
    private UserEligibleBenefitsData data;

    @Builder
    @Getter
    public static class UserEligibleBenefitsData {
        @Analysed
        private final Collection<EligibleBenefit> eligibleBenefits;

        @Setter
        @Analysed
        private String sid;
        @Setter
        @Analysed
        private String redirectUrl;
    }

}
