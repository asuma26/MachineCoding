package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.wynk.vas.client.enums.UserType;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsisdnOperatorDetails {
    @JsonIgnore
    private static final MsisdnOperatorDetails DEFAULT_INSTANCE = new MsisdnOperatorDetails();
    private String msisdn;
    @JsonProperty("altsearchDslInfo")
    private UserDslInfo userDslInfo;
    @JsonProperty("altsearchDthInfo")
    private UserDthInfo userDthInfo;
    @JsonProperty("ndsUserInfo")
    private UserMobilityInfo userMobilityInfo;
    private DslDetails dslDetails;

    @JsonIgnore
    public static MsisdnOperatorDetails defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    @JsonIgnore
    public MultiValuedMap<UserType, String> getAllUserTypes() {
        MultiValuedMap<UserType, String> userTypes = new HashSetValuedHashMap<>();
        if (userDslInfo != null && StringUtils.equalsIgnoreCase(userDslInfo.getStatus(), "ACTIVE") && StringUtils.isNotBlank(userDslInfo.getDslId())) {
            userTypes.put(UserType.BROADBAND, userDslInfo.getDslId());
        }
        if (userDthInfo != null && StringUtils.equalsIgnoreCase(userDthInfo.getStatus(), "ACTIVE") && CollectionUtils.isNotEmpty(userDthInfo.getDthIds())) {
            userTypes.putAll(UserType.DTH, userDthInfo.getDthIds());
        }
        if (userMobilityInfo != null && userMobilityInfo.getUserType() != null) {
            userTypes.put(userMobilityInfo.getUserType(), this.msisdn);
        }
        return userTypes;
    }

    @JsonIgnore
    public Set<String> getAllSI() {
        Set<String> allSi = new HashSet<>();
        if (userDslInfo != null && StringUtils.equalsIgnoreCase(userDslInfo.getStatus(), "ACTIVE")) {
            allSi.add(userDslInfo.getDslId());
        }
        if (userDthInfo != null && StringUtils.equalsIgnoreCase(userDthInfo.getStatus(), "ACTIVE")) {
            allSi.addAll(userDthInfo.getDthIds());
        }
        return allSi;
    }
}
