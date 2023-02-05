package in.wynk.subscription.common.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {

    private long lowestValidity;
    private List<Integer> offerId;
    private List<Integer> activePlanId;
    private Map<String, List<String>> segments;

}
