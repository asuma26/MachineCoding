package in.wynk.subscription.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.constant.BaseConstants;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@AnalysedEntity
public enum ProvisionState {

    SUBSCRIBED("SUBSCRIBED"),
    DEFERRED("DEFERRED"),
    UNSUBSCRIBED("UNSUBSCRIBED"),
    MIGRATED("MIGRATED"),
    UNKNOWN("UNKNOWN");

    @Analysed(name = BaseConstants.PROVISION_STATE)
    private final String provisionState;

}
