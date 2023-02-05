package in.wynk.subscription.core.dao.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class OfferPlanMapping {

    private Integer newOfferId;
    private Integer newPlanId;

}
