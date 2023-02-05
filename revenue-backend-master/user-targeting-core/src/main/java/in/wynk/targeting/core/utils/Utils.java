package in.wynk.targeting.core.utils;

import in.wynk.targeting.core.constant.AdConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import static in.wynk.targeting.core.constant.UTLoggingMarkers.UTILS_ERROR;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getDeviceId(String did) {
        String deviceId = null;
        if (did != null) {
            String[] res = did.split("\\s*/\\s*");
            deviceId = res[0];
        }
        return deviceId;
    }

    public static String getUidFromHeader(String utknHeader){
        String uid = EMPTY;
        if(!StringUtils.isEmpty(utknHeader)){
            uid = utknHeader.split(AdConstants.COLON)[0];
        } else{
            logger.error(UTILS_ERROR, "Unable to get uid from {}", utknHeader);
        }
        return uid;
    }

}
