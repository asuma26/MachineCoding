package in.wynk.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static in.wynk.common.constant.BaseConstants.TRANSACTION_STATUS;

@Getter
@AnalysedEntity
@AllArgsConstructor
public enum TransactionStatus {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    FAILUREALREADYSUBSCRIBED("FAILUREALREADYSUBSCRIBED"),
    INPROGRESS("INPROGRESS"),
    ONHOLD("ONHOLD"),
    CANCELLED("CANCELLED"),
    TIMEDOUT("TIMEDOUT"),
    DEFFERED_ACTIVATION("DEFFERED_ACTIVATION"),
    REFUNDED("REFUNDED"),
    OFFLINE("OFFLINE"),
    UNKNOWN("UNKNOWN"),
    MIGRATED("MIGRATED");

    @Analysed(name = TRANSACTION_STATUS)
    private final String value;
}
