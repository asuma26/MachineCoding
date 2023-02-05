package in.wynk.subscription.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.subscription.enums.Os;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@AnalysedEntity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferProvisionRequest {
    @Analysed
    private String uid;
    @Analysed
    private String appVersion;
    @Analysed
    private Integer buildNo;
    @Analysed
    private String msisdn;
    @Analysed
    private String os;
    @Analysed
    private String deviceId;
    @Analysed
    private String deviceType;
    @Analysed
    private String appId;
    @Analysed
    private String service;
    @Analysed
    private long createdTimestamp;

    public WynkService getService() {
        return WynkService.fromString(this.service);
    }

    public void setService(String service) {
        this.service = service;
    }

    public AppId getAppId() {
        return AppId.fromString(this.appId);
    }

    public Os getOs() {
        return Os.getOsFromValue(this.os);
    }

    public void setMsisdn(String msisdn){
        if(StringUtils.isNotEmpty(msisdn)){
            this.msisdn = MsisdnUtils.normalizePhoneNumber(msisdn);
        }
    }
}
