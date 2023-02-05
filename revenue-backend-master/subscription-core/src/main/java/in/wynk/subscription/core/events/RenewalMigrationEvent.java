package in.wynk.subscription.core.events;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AnalysedEntity
public class RenewalMigrationEvent {

    @Analysed
    private String uid;
    @Analysed
    private String paymentCode;
    @Analysed
    private Date nextChargingDate;
    @Analysed
    @Builder.Default
    private Map<String, String> paymentMetaData = new HashMap<>();

}
