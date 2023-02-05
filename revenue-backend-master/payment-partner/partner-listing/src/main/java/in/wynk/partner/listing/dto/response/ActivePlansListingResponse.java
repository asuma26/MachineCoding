package in.wynk.partner.listing.dto.response;

import in.wynk.partner.listing.dto.UserActivePlan;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Abhishek
 * @created 07/09/20
 */

@Getter
@Builder
public class ActivePlansListingResponse {

    private final List<UserActivePlan> activePlans;

}
