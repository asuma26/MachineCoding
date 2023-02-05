package in.wynk.payment.dto.request;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class RefundTransactionReconciliationStatusRequest extends AbstractTransactionReconciliationStatusRequest {

    @Analysed
    private String originalTransactionId;

}
