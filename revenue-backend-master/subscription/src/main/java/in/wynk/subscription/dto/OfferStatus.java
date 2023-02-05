package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.enums.ProvisionType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AnalysedEntity
public abstract class OfferStatus {
    @Analysed
    private final int offerId;
    private final String title;
    @Analysed
    private final ProvisionType type;
    @Analysed
    private final String status;
    @Analysed
    private final List<Integer> planIds;
}