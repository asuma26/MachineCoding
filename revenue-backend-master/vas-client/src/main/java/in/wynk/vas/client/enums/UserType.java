package in.wynk.vas.client.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 17/06/20
 */
public enum UserType {
    PREPAID("prepaid"),
    POSTPAID("postpaid"),
    UNKNOWN("unknown"),
    BROADBAND("broadband"),
    DTH("dth");

    private String value;

    UserType(String value) {
        this.value = value;
    }

    @Analysed(name = "userType")
    public String getValue() {
        return this.value;
    }

    public static UserType fromValue(String type) {
        for(UserType userType : values()) {
            if(StringUtils.equalsIgnoreCase(userType.getValue(), type)) {
                return userType;
            }
        }
        return UNKNOWN;
    }

}
