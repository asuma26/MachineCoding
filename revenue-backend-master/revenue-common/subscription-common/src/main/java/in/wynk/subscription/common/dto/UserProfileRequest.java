package in.wynk.subscription.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

@Getter
@Builder
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileRequest {

    @Analysed
    private String uid;
    @Analysed
    private String msisdn;
    @Analysed
    private String service;
}
