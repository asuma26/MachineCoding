package in.wynk.partner.listing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek
 * @created 07/09/20
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EligiblePlansListingRequest {
    private String clientId;
    private String service;
}
