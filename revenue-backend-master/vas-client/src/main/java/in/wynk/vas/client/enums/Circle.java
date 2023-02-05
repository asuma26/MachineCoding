package in.wynk.vas.client.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 22/06/20
 */
@AnalysedEntity
public enum Circle {

    PUNJAB("pb", "Punjab"),
    DELHI("dl", "Delhi"),
    MUMBAI("mb", "Mumbai"),
    KARNATAKA("ka", "Karnataka"),
    KOLKATA( "ko", "Kolkata"),
    CHENNAI("ch", "Chennai"),
    KERALA("kl", "Kerala"),
    MADHYAPRADESH("mp", "MP and Chattisgarh"),
    UPWEST("uw", "UP West and Uttaranchal"),
    GUJARAT("gj", "Gujarat"),
    MAHARASHTRA("mh", "Maharashtra and Goa"),
    ANDHRAPRADESH("ap", "Andhra Pradesh"),
    HIMACHALPRADESH("hp", "Himachal Pradesh"),
    TAMILNADU("tn", "Tamil Nadu"),
    HARYANA("hr", "Haryana"),
    WESTBENGAL( "wb", "West Bengal"),
    BIHAR("bh", "Bihar and Jharkhand"),
    ORISSA("or", "Orissa"),
    UPEAST("ue", "UP East"),
    ASSAM("as", "Assam"),
    JAMMUKASHMIR("jk", "Jammu and Kashmir"),
    RAJASTHAN("rj", "Rajasthan"),
    NORTHEAST("ne", "North East"),
    ANDAMANNICOABAR("an", "Andaman Nicobar"),
    UNINOR("uni", "Uninor Airtel"),
    DOCOMO("doc", "Docomo Airtel"),
    SRILANKA("sl","Sri Lanka"),
    UNKNOWN("unknown", "UNKNOWN"),

    // PAN added for AirtelStore
    PAN("pan", "PAN");

    Circle(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    private final String shortName;
    private final String fullName;


    private static final Map<String, Circle> fullNameMapping = new HashMap<>();

    private static final Map<String, Circle> shortNameMapping = new HashMap<>();

    static {
        for(Circle circle : Circle.values()) {
            fullNameMapping.put(StringUtils.lowerCase(circle.getFullName()), circle);
            shortNameMapping.put(StringUtils.lowerCase(circle.getShortName()), circle);
        }
    }

    @Analysed(name = "circle")
    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public static Circle getCircleFromShortName(String shortName) {
        Circle circle = Circle.UNKNOWN;
        if(StringUtils.isNotBlank(shortName)) {
            circle = shortNameMapping.get(StringUtils.lowerCase(shortName));
        }
        return circle;
    }

    public static Circle getCircleFromFullName(String fullName) {
        Circle circle = Circle.UNKNOWN;
        if(StringUtils.isNotBlank(fullName)) {
            circle = fullNameMapping.get(StringUtils.lowerCase(fullName));
        }
        return circle;
    }

    public static Map<String, Circle> getFullNameMapping() {
        return fullNameMapping;
    }

    public static Map<String, Circle> getShortNameMapping() {
        return shortNameMapping;
    }
}
