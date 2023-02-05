package in.wynk.common.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Component
public class MsisdnUtils {

    private static final Logger logger = LoggerFactory.getLogger(MsisdnUtils.class.getCanonicalName());


    private static String key;

    private static final int MSN_UUV0_SIZE_MAX = 17;
    private static final int MSN_UUV1_SIZE_MAX = 27;

    public static String getTenDigitMsisdn(String msisdn) {
        if(StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }
        msisdn = msisdn.trim();
        int length = msisdn.length();
        if(length == 10) {
            return msisdn;
        }
        if(length > 10) {
            return msisdn.substring(length - 10);
        }
        throw new IllegalArgumentException("Illegal value for msisdn : " + msisdn);
    }

    public static String get12DigitMsisdn(String msisdn) {
        if(StringUtils.isNotEmpty(msisdn)) {
            if(msisdn.length() == 10) {
                return "91" + msisdn;
            }
            else if(msisdn.startsWith("+")) {
                //valid for all numbers starting with + //Srilanka mobile number length is 9
                return msisdn.substring(1);
            }
        }
        return msisdn;
    }

    public static boolean isAirtelSriLanka(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn)) {
            return (msisdn.startsWith("+9475") && msisdn.length() == 12) || (msisdn.startsWith("9475") && msisdn.length() == 11);
        }
        return false;
    }

    public static boolean isSriLanka(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn)) {
            return (msisdn.startsWith("+94") && msisdn.length() == 12) || (msisdn.startsWith("94") && msisdn.length() == 11);
        }
        return false;
    }

    public static boolean containsAlpha(String str) {
        if(str == null) {
            return false;
        }
        for(int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if(Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    public static String normalizePhoneNumber(String ph) {
        if(ph == null || ph.isEmpty() || (ph.startsWith("+") && !containsAlpha(ph))) {
            return ph;
        }
        try {
            if(containsAlpha(ph)) {
                return null;
            }
            else if(ph.startsWith("94") && ph.length() == 11) {
                // if SriLankan number
                return "+" + ph;
            }
            else if(ph.startsWith("91") && ph.length() == 12) {
                return "+" + ph;
            }
            else if(ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.

            // 09811920234
            {
                return "+91" + ph.substring(1);
            }
            else if(ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2);
            }
            else if(ph.length() == 10) {
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(ph, "IN");
                return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
            else {
                return null;
            }
        }
        catch (NumberParseException e) {
            // this can also be the case if we use number like "TD-HIKE"
            // fallback to the raw parsing
            if(ph.startsWith("94") && ph.length() == 11) {
                // if SriLankan number
                return "+" + ph;
            }
            else if(ph.length() == 10) {
                return "+91" + ph;
            }
            else if(ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
            // 09811920234
            {
                return "+91" + ph.substring(1);
            }

            else if(ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2);
            }

            return null;
        }

    }

    private static byte[] hmacsha1(String key, String in) throws Exception {
        SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sk);
        return mac.doFinal(in.getBytes());
    }

    private static String base64(byte[] hmacsha1_data) {

        return new String(Base64.encodeBase64(hmacsha1_data));
    }


    private static String hmacSha1Enc(String key, String in, int max) throws Exception {
        if(max > 0) {
            String Base64Data = base64(hmacsha1(key, in));
            String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");

            if(Base64urlencodeData.length() > max) {
                return Base64urlencodeData.substring(0, max);
            }
            return Base64urlencodeData;
        }

        return "";
    }

    public static String getUidFromMsisdn(String msisdn){
        return getUid(msisdn, null);
    }

    public static String getUidFromDeviceId(String deviceId){
        return getUid(null, deviceId);
    }

    private static String getUid(String msisdn, String deviceId) {
        String uuid = null;
        try {
            if(StringUtils.isNotBlank(msisdn)) {
                msisdn = normalizePhoneNumber(msisdn);
                uuid = hmacSha1Enc(key, msisdn, MSN_UUV0_SIZE_MAX) + "0";
            }
            else if(StringUtils.isNotBlank(deviceId)) {
                uuid = hmacSha1Enc(key, deviceId, MSN_UUV1_SIZE_MAX) + "2";
            }
            else {
                logger.error("Both msisdn and deviceId are blank");
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return uuid;

    }

    @Value("${uid.hash.key}")
    public void setKet(String encKey) {
        key = encKey;
    }

}
