package in.wynk.subscription.core.events;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.enums.TransactionStatus;
import in.wynk.common.enums.WynkService;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AnalysedEntity
public class SubscriptionEvent {
    @Analysed
    private final int offerId;
    @Analysed
    private final int planId;
    @Analysed
    private final long validTillDate;
    @Analysed
    private final long deferredUntil;
    @Analysed
    private final String uid;
    @Analysed
    private final String msisdn;
    @Analysed
    private final String referenceId;
    @Analysed
    private final String paymentPartner;
    @Analysed
    private final boolean active;
    @Analysed
    private final boolean autoRenewal;
    @Analysed
    private final WynkService service;
    @Analysed
    private final PaymentEvent event;
    @Analysed
    private final TransactionStatus status;

}
