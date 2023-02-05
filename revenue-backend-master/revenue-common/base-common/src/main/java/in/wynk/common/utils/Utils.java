package in.wynk.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final String        TEXT_PLAIN       = "text/plain; charset=UTF-8";
    private static final String        APPLICATION_JSON = "application/json; charset=UTF-8";


    private static final Gson gson = new GsonBuilder().create();

    public static Gson getGson() {
        return gson;
    }

    public static String encodeBase64(String key) {
        return Base64.encodeBase64String(key.getBytes());
    }

    public static String getTenDigitMsisdn(String msisdn) {
        if (StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }
        msisdn = msisdn.trim();
        int length = msisdn.length();
        if (length == 10) {
            return msisdn;
        }
        if (length > 10) {
            return msisdn.substring(length - 10);
        }
        throw new IllegalArgumentException("Illegal value for msisdn : " + msisdn);
    }



    public static String getStringParameter(Map<String, String> urlParameters, String paramName) {
        if(urlParameters == null) {
            return null;
        }
        String valueParam = urlParameters.get(paramName);
        if(StringUtils.isNotEmpty(valueParam)) {
            return valueParam.trim();
        }
        return null;
    }

    public static long getLongParameter(Map<String, String> urlParameters, String paramName, long defaultValue) {
        String stringParameter = getStringParameter(urlParameters, paramName);
        try {
            if(StringUtils.isNotBlank(stringParameter)) {
                defaultValue = Long.parseLong(stringParameter);
            }
        }
        catch (Exception e) {
            logger.error("Exception while extracting long parameter", e);
        }
        return defaultValue;
    }

}


