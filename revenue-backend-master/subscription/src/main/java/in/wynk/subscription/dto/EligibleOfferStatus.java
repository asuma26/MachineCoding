package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class EligibleOfferStatus extends OfferStatus {
    private final boolean freeTrialAvailable;
}
