package in.wynk.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    public static String getFormattedDate(long dateInMs, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(dateInMs));
    }

    public static String generateHash(String text, String algo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algo);
        md.update(text.getBytes());
        byte[] byteData = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte byteDatum : byteData) {
            sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getStringParameter(Map<String, List<String>> urlParameters, String paramName) {
        if(urlParameters == null || !urlParameters.containsKey(paramName)) {
            return null;
        }
        List<String> paramList = urlParameters.get(paramName);
        String nresultsParam = paramList.get(0);
        if(nresultsParam != null) {
            return nresultsParam.trim();
        }
        return null;
    }
}
