package in.wynk.subscription.dto;

import in.wynk.subscription.enums.LoginChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Abhishek
 * @created 02/07/20
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
public class SourceIdentificationResponse {
    @Builder.Default
    private String msisdn = null;
    private LoginChannel loginChannel;
}
