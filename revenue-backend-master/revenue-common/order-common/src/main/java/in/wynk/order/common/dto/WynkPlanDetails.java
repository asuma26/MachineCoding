package in.wynk.order.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AnalysedEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WynkPlanDetails {

    @Analysed
    private final int planId;
    @Analysed
    private final long endDate;
    @Analysed
    private final long startDate;
    @Analysed
    private final boolean autoRenew;

}
