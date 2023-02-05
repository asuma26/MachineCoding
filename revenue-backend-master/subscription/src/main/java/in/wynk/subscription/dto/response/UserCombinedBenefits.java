package in.wynk.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Setter;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
@AnalysedEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCombinedBenefits {

    @Analysed
    private String msisdn;
    @Analysed
    private List<ActiveBenefit> activeBenefits;
    @Analysed
    private Collection<EligibleBenefit> eligibleBenefits;

    @Setter
    @Analysed
    private String sid;
    @Setter
    @Analysed
    private String redirectUrl;
}
