package in.wynk.subscription.event;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Abhishek
 * @created 15/07/20
 */
@Getter
@Builder
@AnalysedEntity
public class NotificationEvent {
    @Analysed
    private final String priority;
    @Analysed
    private final String message;
    @Analysed
    private final String msisdn;
    @Analysed
    private final String service;
}
