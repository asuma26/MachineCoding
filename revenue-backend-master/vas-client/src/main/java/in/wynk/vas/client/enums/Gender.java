package in.wynk.vas.client.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhishek
 * @created 17/06/20
 */
public enum Gender {
    MALE("male", "M"), FEMALE("female", "F"), TRANS("trans", "T"), UNAVAILABLE("unavailable", "");

    private String value;

    private  String shortCode;

    Gender(String value, String shortCode) {
        this.value = value;
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromShortValue(String genderValue) {
        for(Gender gender : values()) {
            if(StringUtils.equalsIgnoreCase(gender.getShortCode(), genderValue)) {
                return gender;
            }
        }
        return UNAVAILABLE;
    }

    public static Gender fromValue(String genderValue) {
        for(Gender gender : values()) {
            if(StringUtils.equalsIgnoreCase(gender.getValue(), genderValue)) {
                return gender;
            }
        }
        return UNAVAILABLE;
    }
}
