package in.wynk.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.sms.common.constant.SMSPriority;
import in.wynk.sms.common.constant.SMSSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@JsonIgnoreProperties(value = "true")
@Deprecated
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AnalysedEntity
public class SendSmsRequest implements Serializable {

    private static final long serialVersionUID = -289736810127565940L;
    @Analysed
    private String priority;
    @Analysed
    private String message;
    @Analysed
    private String msisdn;
    @Analysed
    private String source;
    private boolean nineToNine = false;
    private boolean useDnd = false;
    private Integer retryCount;
    @Analysed
    private String countryCode;
    //Added service field to find out messages being sent by CAPI.
    @Analysed
    private String service;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = message.replaceAll("[^\\x00-\\x7F]", "");
        message = StringEscapeUtils.unescapeJava(message);
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean getUseDnd() {
        return useDnd;
    }

    public void setUseDnd(boolean useDnd) {
        this.useDnd = useDnd;
    }

    public boolean isNineToNine() {
        return nineToNine;
    }

    public void setNineToNine(boolean nineToNine) {
        this.nineToNine = nineToNine;
    }

    public String getSource() {
        return source;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @JsonIgnore
    public boolean validRequest() {
        if (StringUtils.isAnyBlank(message, priority, msisdn, source)) {
            return false;
        }
        if (!SMSSource.isValidSource(source)) {
            return false;
        }
        return EnumUtils.isValidEnum(SMSPriority.class, priority);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("priority", priority)
                .append("message", message)
                .append("msisdn", msisdn)
                .append("source", source)
                .append("nineToNine", nineToNine)
                .append("useDnd", useDnd)
                .append("retryCount", retryCount)
                .append("countryCode", countryCode)
                .append("service", service)
                .toString();
    }
}
