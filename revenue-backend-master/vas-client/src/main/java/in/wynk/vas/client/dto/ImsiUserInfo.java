package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Abhishek
 * @created 01/07/20
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsiUserInfo {
    private String imsi;
    private String msisdn;
    private String circle;
    private String userType;
    private String errorCode;
    private String errorMessage;
}