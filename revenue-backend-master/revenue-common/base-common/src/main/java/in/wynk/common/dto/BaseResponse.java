package in.wynk.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.MDC;

import static in.wynk.logging.constants.LoggingConstants.REQUEST_ID;


@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseResponse<R> {
    @Analysed
    @JsonProperty
    @Builder.Default
    private final boolean success = true;
    @Analysed
    @JsonProperty
    private String message;
    @Analysed
    @JsonProperty
    @Builder.Default
    private final String rid = MDC.get(REQUEST_ID);

    @JsonProperty
    public abstract R getData();

}
