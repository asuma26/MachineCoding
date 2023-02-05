package in.wynk.subscription.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.enums.Os;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Abhishek
 * @created 24/06/20
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MsisdnIdentificationRequest {
    @Analysed
    private List<String> imsi;
    @Analysed
    private String xMsisdn;
    @Analysed
    private String appVersion;
    @Analysed
    private String os;
    @Analysed
    private String deviceId;
    @Analysed
    private String service;
    @Analysed
    private String dthCustID;
    @Analysed
    private String appId;
    @Analysed
    private String deviceType;
    @Analysed
    private Integer buildNo;

    public String getxMsisdn() {
        return this.xMsisdn;
    }

    public AppId getAppId() {
        return AppId.fromString(this.appId);
    }

    public Os getOs() {
        return Os.getOsFromValue(this.os);
    }

    public WynkService getService() {
        return WynkService.fromString(this.service);
    }
}
