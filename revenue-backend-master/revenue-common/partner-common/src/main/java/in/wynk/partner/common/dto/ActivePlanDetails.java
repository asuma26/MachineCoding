package in.wynk.partner.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Abhishek
 * @created 07/09/20
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivePlanDetails {
    private int planId;
    private Date validTillDate;
    private String paymentChannel;
}
