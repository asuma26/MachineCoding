package in.wynk.subscription.dto;

import in.wynk.common.enums.AppId;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.OfferDeviceMapping;
import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.enums.Os;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@NoArgsConstructor
public class OfferContext {
    private WynkService service;
    private Offer offer;
    private String msisdn;
    private String deviceId;
    private Os os;
    private MsisdnOperatorDetails msisdnOperatorDetails;
    private Integer buildNo;
    private AppId appId;
    private List<UserPlanDetails> userPlanDetails;
    private List<OfferDeviceMapping> offerDeviceMappings;
    private Map<Integer, Integer> activeOffers;
    private Map<String, List<ThanksUserSegment>> allThanksSegments;

    public OfferContext(OfferEligibilityCheckRequest request) {
        this.service = request.getWynkService();
        this.msisdn = request.getMsisdn();
        this.deviceId = request.getDeviceId();
        this.os = request.getOs();
        this.msisdnOperatorDetails = request.getMsisdnOperatorDetails();
        this.buildNo = request.getBuildNo();
        this.appId = request.getAppId();
        this.activeOffers = request.getActiveOfferToPlanMap();
        this.allThanksSegments = request.getAllThanksSegment();
        this.userPlanDetails = request.getUserPlanDetails();
    }

    public OfferContext(OfferContext oldOfferContext) {
        this.service = oldOfferContext.getService();
        this.msisdn = oldOfferContext.getMsisdn();
        this.deviceId = oldOfferContext.getDeviceId();
        this.os = oldOfferContext.getOs();
        this.msisdnOperatorDetails = oldOfferContext.getMsisdnOperatorDetails();
        this.buildNo = oldOfferContext.getBuildNo();
        this.appId = oldOfferContext.getAppId();
        this.activeOffers = oldOfferContext.getActiveOffers();
        this.allThanksSegments = oldOfferContext.getAllThanksSegments();
        this.offerDeviceMappings = oldOfferContext.getOfferDeviceMappings();
        this.userPlanDetails = oldOfferContext.getUserPlanDetails();
    }

    public List<UserPlanDetails> getUserPlanDetails(){
        if (Objects.nonNull(userPlanDetails)) {
            return userPlanDetails;
        }
        return new ArrayList<>();
    }

    public List<OfferDeviceMapping> getOfferDeviceMappings() {
            return offerDeviceMappings;
    }
}
