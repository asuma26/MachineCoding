package in.wynk.subscription.core.dao.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
@Builder
public class FilterUserDetails {
    @Id
    private String msisdn;
}
