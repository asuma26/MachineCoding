package in.wynk.thanks.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;

@Deprecated //Moved to Revenue Commons
public class ThanksUtils {

    //TODO: move the code to common utils jar.

    private static final Logger logger = LoggerFactory.getLogger(ThanksUtils.class);
    private static final int MSN_UUV0_SIZE_MAX = 17;
    private static final int MSN_UUV1_SIZE_MAX = 27;
    private static final String KEY = "81BHyAUfMgCiu9I7XqArF1Bvy0o";

    public static String generateAppToken(String uid, String secretKey) {
        try {
            return calculateRFC2104HMAC(uid, secretKey);

        } catch (Exception e) {
            logger.error("Error while generating apptoken ", e);
            return null;
        }
    }

    public static String calculateRFC2104HMAC(String data, String secretKey) throws java.security.SignatureException {
        String result;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            mac.init(key);
            byte[] authentication = mac.doFinal(data.getBytes());
            result = new String(org.apache.commons.codec.binary.Base64.encodeBase64(authentication));

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    private static String hmacSha1Enc(String in, int max) throws Exception {
        if (max > 0) {
            String Base64Data = base64(hmacsha1(in));
            String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");

            if (Base64urlencodeData.length() > max) {
                return Base64urlencodeData.substring(0, max);
            }
            return Base64urlencodeData;
        }

        return "";
    }

    private static byte[] hmacsha1(String in) throws Exception {
        try {
            SecretKeySpec sk = new SecretKeySpec(ThanksUtils.KEY.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
            return mac.doFinal(in.getBytes());
        } catch (Exception e) {
            logger.error("Exception occuring in UserCoreUtils is: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static String base64(byte[] hmacsha1_data) {
        return new String(Base64.encodeBase64(hmacsha1_data));
    }


    public static String generateUID(String msisdn, String deviceId) {
        String uuid = null;
        try {
            if (StringUtils.isNotBlank(msisdn)) {
                String tempMsisdn = normalizePhoneNumber(msisdn);
                if (StringUtils.isEmpty(tempMsisdn)) {
                    throw new WynkRuntimeException(ThanksLoggingMarkers.UUID_GENERATION_ERROR, "Failed to generate UUID", new IllegalArgumentException("msisdn is invalid: " + msisdn));
                }
                msisdn = tempMsisdn;
                uuid = hmacSha1Enc(msisdn, MSN_UUV0_SIZE_MAX) + "0";
            } else if (StringUtils.isNotBlank(deviceId)) {
                uuid = hmacSha1Enc(deviceId, MSN_UUV1_SIZE_MAX) + "2";
            } else {
                logger.error("Both msisdn and deviceId are blank");
            }
        } catch (Throwable e) {
            throw new WynkRuntimeException(ThanksLoggingMarkers.UUID_GENERATION_ERROR, "Failed to generate UUID ", e);
        }
        return uuid;

    }

    public static String normalizePhoneNumber(String ph) {
        if (ph == null || ph.isEmpty() || (ph.startsWith("+") && ph.length() == 13 && !containsAlpha(ph))) {
            return ph;
        }

        try {
            if (containsAlpha(ph)) {
                return null;
            } else if (ph.startsWith("91") && ph.length() == 12) {
                return "+" + ph;
            } else if (ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
            // 09811920234
            {
                return "+91" + ph.substring(1);
            } else if (ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2);
            } else if (ph.length() == 10) {
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(ph, "IN");
                return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            } else {
                return null;
            }
        } catch (NumberParseException e) {
            // this can also be the case if we use number like "TD-HIKE"
            // fallback to the raw parsing
            if (ph.length() == 10) {
                return "+91" + ph;
            } else if (ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
            // 09811920234
            {
                return "+91" + ph.substring(1);
            } else if (ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2);
            }
            return null;
        }

    }

    public static boolean containsAlpha(String str) {
        if (str == null) {
            return false;
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }
}
