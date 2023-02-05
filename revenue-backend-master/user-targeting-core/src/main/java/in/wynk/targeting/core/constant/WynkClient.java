package in.wynk.targeting.core.constant;

import in.wynk.common.constant.BaseConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Deprecated
@AllArgsConstructor
public enum WynkClient implements Serializable {

    MUSIC_APP_ANDROID("MUSIC_APP", "ANDROID","music"),
    MUSIC_APP_IOS("MUSIC_APP", "IOS","music"),
    MUSIC_MWEB("MUSIC_WEB", "MOBILE","music"),
    MUSIC_WEB("MUSIC_WEB", "LARGE_SCREEN","music"),
    ATV_APP_ANDROID("ATV_APP", "ANDROID","airteltv"),
    ATV_APP_IOS("ATV_APP", "IOS","airteltv"),
    ATV_APP_MWEB("ATV_WEB", "BROWSER","airteltv"),
    ATV_APP_WEB("ATV_APP", "BROWSER","airteltv"),
    XSTREAM_APP_ANDROID("XSTREAM_APP", "ANDROID","airteltv"),
    XSTREAM_APP_IOS("XSTREAM_APP", "IOS","airteltv"),
    XSTREAM_APP_MWEB("XSTREAM_WEB", "MOBILE","airteltv"),
    XSTREAM_APP_WEB("XSTREAM_WEB", "LARGE_SCREEN","airteltv"),
    XSTREAM_LARGE_SCREEN("XSTREAM_LS_APP", "ANDROID","airteltv"),
    XSTREAM_FIRESTICK("XSTREAM_FS_APP", "ANDROID","airteltv"),
    BOOKS_APP_ANDROID("BOOKS_APP", "ANDROID","books"),
    BOOKS_APP_IOS("BOOKS_APP", "IOS","books"),
    AIRTEL_THANKS("AIRTEL_THANKS", "ANDROID", "airteltv"),
    UNKNOWN("", "","");

    private final String id;
    private final String os;
    private final String service;

    public static WynkClient lookup(String id, String os) {
        WynkClient wynkClient = UNKNOWN;
        for (WynkClient client : WynkClient.values()) {
            if (client.getId().equalsIgnoreCase(id) && client.getOs().equalsIgnoreCase(os)) {
                wynkClient = client;
                break;
            }
        }
        return wynkClient;
    }

    public String getClientDetails() {
        return id + BaseConstants.DELIMITER + os;
   }

   public static Boolean isMusicClient(WynkClient client) {
       return client.equals(MUSIC_APP_ANDROID) || client.equals(MUSIC_APP_IOS) || client.equals(MUSIC_MWEB) || client.equals(MUSIC_WEB);
   }

}
