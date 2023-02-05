package in.wynk.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.dto.BaseResponse;
import in.wynk.subscription.dto.OfferStatus;
import in.wynk.subscription.dto.SubscriptionStatus;
import in.wynk.subscription.dto.Userdata;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Abhishek
 * @created 17/06/20
 */

@Getter
@SuperBuilder
@AnalysedEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferProvisionResponse extends BaseResponse<OfferProvisionResponse.OfferProvisionData> {

    private OfferProvisionData data;

    @Builder
    @Getter
    public static class OfferProvisionData {
        @Analysed
        private final Userdata userdata;

        @Analysed
        private final List<SubscriptionStatus> subscriptionStatus;

        @Analysed
        private final List<OfferStatus> offerStatus;
    }
}
