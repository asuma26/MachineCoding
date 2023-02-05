package in.wynk.subscription.core.dao.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DescriptionObject {
    String desc;
    Boolean value;
}