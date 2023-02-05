package in.wynk.subscription.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PlanProvisioningRequest {

    @Analysed
    private String uid;
    @Analysed
    private String msisdn;
    @Analysed
    private String referenceId;
    @Analysed
    private String paymentCode;
    @Analysed
    private String paymentPartner;
    @Analysed
    private PaymentEvent eventType;

}
