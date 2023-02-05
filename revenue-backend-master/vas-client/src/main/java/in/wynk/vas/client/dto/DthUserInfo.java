package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Abhishek
 * @created 01/07/20
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DthUserInfo {
    private String accountID;
    private String siNumber;
    private String customerName;
    private String accountBalance;
    private String planId;
    private String stbId;
    private String casID;
    private String componentId;
    private String internalId;
    private String rtn;
    private String serverId;
    private String subscriberNumber;
    private String errorMessage;
}
