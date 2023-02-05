package in.wynk.partner.listing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek
 * @created 24/09/20
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivePlan {
    private long validTillDate;
    private int planId;
    private String paymentChannel;
}
