package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class ActiveOfferStatus extends OfferStatus {
    @Analysed
    private final long validTill;
    @Analysed
    private final boolean autoRenew;
}
