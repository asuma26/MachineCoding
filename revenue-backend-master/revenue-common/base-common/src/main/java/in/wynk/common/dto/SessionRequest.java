package in.wynk.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.Os;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.MsisdnUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Getter
@AnalysedEntity
public class SessionRequest {
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
    private Double itemPrice;
    @Analysed
    private String service;
    @Analysed
    private String itemId;
    @Analysed
    private String packGroup;
    @Analysed
    private String successUrl;
    @Analysed
    private String failureUrl;
    @Analysed
    private Map<String, String> params;
    @Analysed
    private long createdTimestamp;
    @Analysed
    private String theme;
    @Analysed
    private String intent;
    @Analysed
    @Deprecated
    private String ingressIntent;

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

    public String getMsisdn() {
        if (StringUtils.isNotEmpty(msisdn)) {
            return MsisdnUtils.normalizePhoneNumber(msisdn);
        }
        return null;
    }

    public String getIntent() {
        if (StringUtils.isNotBlank(intent))
            return intent;
        else
            return ingressIntent;
    }

}
