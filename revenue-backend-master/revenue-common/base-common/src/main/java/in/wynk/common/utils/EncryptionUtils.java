package in.wynk.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SignatureException;

public class EncryptionUtils {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);
    private static final String ALGORITHM = "AES";

    public static String generateAppToken(String uid, String secretKey) {
        try {
            return calculateRFC2104HMAC(uid, secretKey);

        } catch (Exception e) {
            logger.error("Error while generating apptoken ", e);
            return null;
        }
    }

    public static String generateSHA512Hash(String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(str.getBytes());
            byte[] byteData = md.digest();
            // convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error while digesting string: {}", e.getMessage(), e);
        }
        return null;
    }

    public static String calculateRFC2104HMAC(String data, String secretKey) throws SignatureException {
        String result;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            mac.init(key);
            byte[] authentication = mac.doFinal(data.getBytes());
            result = new String(Base64.encodeBase64(authentication));

        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    public static String encrypt(String valueToEnc, String encKey) throws Exception {
        if (StringUtils.isEmpty(valueToEnc)) {
            return valueToEnc;
        }
        Key key = generateKey(encKey.getBytes(StandardCharsets.UTF_8));
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        return Base64.encodeBase64String(encValue);
    }

    public static String decrypt(String valueToDeenc, String encKey) throws Exception {
        if (StringUtils.isEmpty(valueToDeenc)) {
            return valueToDeenc;
        }
        Key key = generateKey(encKey.getBytes(StandardCharsets.UTF_8));
        byte[] content = Base64.decodeBase64(valueToDeenc.getBytes());
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] data = c.doFinal(content);

        return new String(data);
    }

    private static Key generateKey(byte[] keyBytes) throws Exception {
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

}
