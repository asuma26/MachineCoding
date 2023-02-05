package in.wynk.queue.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public abstract class MessageThresholdExceedEvent {
    @Analysed
    private final String type;
    @Analysed
    private final Integer maxAttempt;
}
