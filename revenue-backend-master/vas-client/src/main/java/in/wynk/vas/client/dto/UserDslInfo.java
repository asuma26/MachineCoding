package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDslInfo {
    private String firstName;
    private String lastName;
    private String customerId;
    private String telephoneNumber;
    private String dslId;
    private String status;
}
