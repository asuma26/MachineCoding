package in.wynk.utils.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

@Getter
@Builder
@AnalysedEntity
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandChannelRequest {
    @Analysed
    private String channelJsonString;

    @Analysed
    private String brandName;
}
