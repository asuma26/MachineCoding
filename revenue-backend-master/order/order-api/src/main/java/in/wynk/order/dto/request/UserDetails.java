package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class UserDetails {

    @Analysed
    @JsonProperty("user_consent")
    private final boolean userConsent;
    @Analysed
    @JsonProperty("user_type")
    private final String userType;
    @Analysed
    @JsonProperty("user_id")
    private final String userId;

    @JsonCreator
    public UserDetails(@JsonProperty("user_consent") boolean userConsent, @JsonProperty("user_type") String userType, @JsonProperty("user_id") String userId) {
        this.userConsent = userConsent;
        this.userType = userType;
        this.userId = userId;
    }
}
