package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.wynk.vas.client.enums.Circle;
import in.wynk.vas.client.enums.Gender;
import in.wynk.vas.client.enums.UserType;
import lombok.Data;
import lombok.ToString;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMobilityInfo {
    private String   location;
    private String   preferredLanguage;
    private String   circle;
    private String userType;
    private String   errorCode;
    private boolean  corporateUser;
    // Needed for YJ since by default it is false and user gets to see free content.
    private boolean  dataUser = true;
    private Gender gender;
    private String   dataRating;
    private Boolean  threeGCapable;
    private Boolean  gprsCapable;
    private String   imei;
    private String   firstName;
    private String   middleName;
    private String   lastName;
    private String   dateOfBirth;
    private String   emailID;
    private String   alternateContactNumber;
    private String   activationDate;
    private String   networkTypeLTE;
    private Boolean  device4gCapable;
    private String   customerType;
    private String   customerClassification;
    private String   customerCategory;
    private String   customerID;
    private String   vasDND;
    //non-null in case of PREPAID
    private Long validity;

//    public boolean isAirtelUser(){
//        Circle c = Circle.getCircleFromFullName(circle);
//        return c != null && c != Circle.UNKNOWN;
//    }

    public UserType getUserType() {
        return UserType.fromValue(this.userType);
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
