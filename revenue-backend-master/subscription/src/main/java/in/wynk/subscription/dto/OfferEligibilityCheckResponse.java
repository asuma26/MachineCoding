package in.wynk.subscription.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.service.OfferCheckEligibility;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static in.wynk.subscription.enums.OfferEligibilityStatus.ELIGIBLE;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferEligibilityCheckResponse {

    private final List<OfferCheckEligibility> offerEligibilityStatus;

    public List<OfferCheckEligibility> getFreeEligibilityResponse() {
        return offerEligibilityStatus.stream().filter(o -> o.getStatus() == ELIGIBLE && o.getOffer().getProvisionType() == ProvisionType.FREE).collect(Collectors.toList());
    }

    public List<OfferCheckEligibility> getPaidEligibilityResponse() {
        return offerEligibilityStatus.stream().filter(o -> o.getStatus() == ELIGIBLE && o.getOffer().getProvisionType() == ProvisionType.PAID).collect(Collectors.toList());
    }

}
