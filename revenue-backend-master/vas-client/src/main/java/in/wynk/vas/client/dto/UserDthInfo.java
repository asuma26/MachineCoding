package in.wynk.vas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDthInfo {
    private String firstName;
    private String lastName;
    private String customerId;
    private List<String> dthIds;
    private String status;
}
