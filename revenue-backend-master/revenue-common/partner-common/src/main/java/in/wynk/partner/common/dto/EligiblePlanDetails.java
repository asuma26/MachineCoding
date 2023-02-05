package in.wynk.partner.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Abhishek
 * @created 07/09/20
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EligiblePlanDetails {
    private boolean isBestValue;
    private Integer month;
    private Integer total;
    private String discount;
    private Integer perMonthValue;
    private Map<String, String> sku;
    private String planTitle;
    @Analysed
    private int planId;
}
