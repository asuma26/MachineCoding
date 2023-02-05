package in.wynk.subscription.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.RequiredArgsConstructor;

@AnalysedEntity
@RequiredArgsConstructor
public enum ProvisionType {

    FREE("FREE"), PAID("PAID");

    @Analysed
    private final String type;

}
