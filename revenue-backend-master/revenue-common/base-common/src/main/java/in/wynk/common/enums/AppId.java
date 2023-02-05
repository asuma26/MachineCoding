package in.wynk.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 20/06/20
 */
public enum AppId {

    MOBILITY("MOBILITY"), SDK("SDK"), XTREME("XTREME"), PRIMETIME("PRIMETIME"),
    PRIMETIMEWEB("PRIMETIMEWEB"), PTMITRA("PTMITRA"), WEB("WEB"), LARGESCREEN("LARGESCREEN"),
    LINUXBOX("LINUXBOX"), WYNKMUSIC("WYNKMUSIC"), THANKS("THANKS"), CHROMECAST("CHROMECAST"),
    FIRESTICK("FIRESTICK"), UNKNOWN("UNKNOWN");;

    private final String value;

    AppId(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static AppId fromString(String appIdStr) {
        if (StringUtils.isBlank(appIdStr)) {
            return UNKNOWN;
        }
        for (AppId appId : values()) {
            if (appId.getValue().equalsIgnoreCase(appIdStr)) {
                return appId;
            }
        }
        return UNKNOWN;
    }
}

