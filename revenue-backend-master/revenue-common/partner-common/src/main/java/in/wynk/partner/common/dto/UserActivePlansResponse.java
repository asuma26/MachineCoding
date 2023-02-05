package in.wynk.partner.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Abhishek
 * @created 01/09/20
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserActivePlansResponse {
    List<ActivePlanDetails> activePlanDetails;
}
