package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@AnalysedEntity
public class AvailablePlans {

    @Analysed
    private final int planId;
    private final boolean isBestValue;
    private final Integer month;
    private final Integer total;
    private final String discount;
    private final Integer perMonthValue;
    private final Map<String, String> sku;
    private final boolean freeTrialAvailable;
    private final Integer displayAmount;
    @Setter
    private String title;
    private final String validityUnit;

}
