package in.wynk.subscription.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.enums.Os;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferEligibilityCheckRequest {
    private WynkService wynkService;
    private String msisdn;
    private MsisdnOperatorDetails msisdnOperatorDetails;
    private Os os;
    private String deviceId;
    private AppId appId;
    private Integer buildNo;
    private Map<Integer, Integer> activeOfferToPlanMap;
    private Map<String, List<ThanksUserSegment>> allThanksSegment;
    private List<UserPlanDetails> userPlanDetails;
}
