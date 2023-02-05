package in.wynk.targeting.core.dao.entity.mongo.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails implements Serializable {
    private static final long serialVersionUID = 8662778756984663139L;
    private String currSegValidity;
    private String lastSyncTime;
    private String gender;
    @JsonProperty(value = "MOBILITY")
    private String mobility;
    private String currSeg;
    private String prevSeg;
    private String createdOn;
    private String operator;
    @JsonProperty(value = "PRIMETIME")
    private String primetime;
    private String acquisitionChannel;
    private String isBroadband;
    private String opInfoUpdatedOn;
    private String wcfOfferUpdatedOn;
    private String isPostpaid;
    private String prevSegValidity;
    @JsonProperty(value = "WEB")
    private String web;
    private String dob;
    private String name;
    private String isXtreme;
    private String userType;
    private String msisdn;
    private String circle;
    private Map<String, String> email;
    @JsonProperty(value = "reg_music")
    private String regMusic;
}
