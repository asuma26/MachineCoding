package in.wynk.subscription.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 20/06/20
 */
public enum Os {
    IOS("ios"),
    ANDROID("android"),
    WEB("web"),
    UNKNOWN("unknown");

    String value;

    Os(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static Os getOsFromValue(String val){
        if (StringUtils.isBlank(val)) {
            return UNKNOWN;
        }
        for (Os os : values()) {
            if (os.getValue().equalsIgnoreCase(val)) {
                return os;
            }
        }
        return UNKNOWN;
    }
}

