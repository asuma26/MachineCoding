package in.wynk.queue.dto;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
public class DefaultMessageThresholdExceedEvent<T> extends MessageThresholdExceedEvent {

    private final T payload;

}
