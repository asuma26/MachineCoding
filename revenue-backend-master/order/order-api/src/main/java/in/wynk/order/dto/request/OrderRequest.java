package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.core.constant.ExecutionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.NONE)
public abstract class OrderRequest {

    @Analysed
    @JsonProperty("mobile_no")
    private final String msisdn;
    @Analysed
    @JsonProperty("callback_url")
    private final String callbackUrl;
    @Analysed
    @Builder.Default
    @JsonIgnore
    private final ExecutionType executionType = ExecutionType.SYNC;

}
