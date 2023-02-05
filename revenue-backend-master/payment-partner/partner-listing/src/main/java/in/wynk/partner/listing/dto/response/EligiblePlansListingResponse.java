package in.wynk.partner.listing.dto.response;

import in.wynk.partner.listing.dto.ClientEligiblePlan;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Abhishek
 * @created 07/09/20
 */

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EligiblePlansListingResponse {

    private List<ClientEligiblePlan> clientEligiblePlans;

}
