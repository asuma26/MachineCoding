package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.dto.BaseResponse;
import in.wynk.subscription.enums.LoginChannel;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Abhishek
 * @created 24/06/20
 */

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentificationResponse extends BaseResponse<IdentificationResponse.MsisdnIdentificationResponse> {

    @Analysed
    private MsisdnIdentificationResponse data;

    @AnalysedEntity
    @Builder
    @Getter
    public static class MsisdnIdentificationResponse {
        @Analysed
        private final String msisdn;
        @Analysed
        private final LoginChannel channel;
        private final MsisdnOperatorDetails operatorDetails;
    }
}
