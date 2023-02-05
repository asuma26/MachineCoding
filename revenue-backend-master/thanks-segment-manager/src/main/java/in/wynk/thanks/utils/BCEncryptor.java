package in.wynk.thanks.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;

@Deprecated //Moved to Revenue Commons
public class BCEncryptor {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final String ALGORITHM_256 = "AES/ECB/PKCS7Padding";
    private static final Logger logger = LoggerFactory.getLogger(BCEncryptor.class);

    public static String encrypt(String valueToEnc, String password) {
        try {
            Key key = generateKey(normalizePassword(password));
            Cipher c = Cipher.getInstance(ALGORITHM_256, "BC");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = c.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));
            String encryptedValue = Base64.encodeBase64String(encValue);
            return encryptedValue;
        } catch (Exception e) {
            logger.error("Exception while encryption : ", e);
            return null;
        }
    }

    public static String decrypt(String encryptedValue, String password) {
        try {
            Key key = generateKey(normalizePassword(password));
            Cipher c = Cipher.getInstance(ALGORITHM_256, "BC");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(encryptedValue);
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;

        } catch (Exception e) {
            logger.error("Exception while decryption : ", e);
            return null;
        }
    }

    private static Key generateKey(String password) throws Exception {
        byte[] keyValue = password.getBytes(StandardCharsets.UTF_8);
        Key key = new SecretKeySpec(keyValue, ALGORITHM_256);
        return key;
    }

    private static String normalizePassword(String password) {
        if (password.length() >= 16) {
            return password.substring(0, 16);
        } else {
            return StringUtils.leftPad(password, 16, '0');
        }

    }

    public static void main(String[] args) {
        String key2 = "59evBzG70Loty56IrkDjJVAJ114=";
        try {
            String str = "+913334445555";
            System.out.println("value " + str);
            String encrypt = BCEncryptor.encrypt(str, key2);
            System.out.println("value " + encrypt);
            String decrypt = BCEncryptor.decrypt(encrypt, key2);
            System.out.println("Decrypted value " + decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
