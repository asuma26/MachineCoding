package in.wynk.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static in.wynk.common.constant.BaseConstants.PAYMENT_EVENT;

@AnalysedEntity
@AllArgsConstructor
@Getter
public enum PaymentEvent {

    RENEW("RENEW"),
    PURCHASE("PURCHASE"),
    DEFERRED("DEFERRED"),
    SUBSCRIBE("SUBSCRIBE"),
    REFUND("REFUND"),
    UNSUBSCRIBE("UNSUBSCRIBE"),
    POINT_PURCHASE("POINT_PURCHASE"),
    TRIAL_SUBSCRIPTION("TRIAL_SUBSCRIPTION");

    @Analysed(name = PAYMENT_EVENT)
    private final String value;

}