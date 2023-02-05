package in.wynk.events.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import static in.wynk.events.constants.EventsLoggingMarkers.UTILS_ERROR;

@Slf4j
public class EventsUtils {

    private static final String ALGORITHM = "AES";
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String decrypt(String valueToDeenc, String encKey) throws Exception {
        if (StringUtils.isEmpty(valueToDeenc)) {
            return valueToDeenc;
        }
        Key key = generateKey(encKey.getBytes(StandardCharsets.UTF_8));

        byte[] content = Base64.decodeBase64(valueToDeenc.getBytes());
        // String encryptedData = content.toString();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] data = c.doFinal(content);
        // String result = Base64.decodeBase64String(data);
        return new String(data);
    }

    private static Key generateKey(byte[] keyBytes) throws Exception {
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String getJsonString(Object object) {
        String json = StringUtils.EMPTY;
        try {
            json = gson.toJson(object);
        } catch (Exception e) {
            log.error(UTILS_ERROR, "Unable to parse: {}", object);
        }
        return json;
    }

    public static JsonObject getJsonObjectFromJsonString(String jsonString) {
        JsonObject jsonObject = null;
        try {
            jsonObject = (JsonObject) JsonParser.parseString(jsonString);
        } catch (Exception e) {
            log.error(UTILS_ERROR, "Error parsing json: {}", jsonString);
        }
        return jsonObject;
    }
}
