package in.wynk.subscription.common.enums;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum PlanType {
    FREE("FREE"),
    FREE_TRIAL("FREE_TRIAL"),
    ONE_TIME_SUBSCRIPTION("ONE_TIME_SUBSCRIPTION"),
    SUBSCRIPTION("SUBSCRIPTION");

    private final String value;

    public String getValue() {
        return name();
    }

    public static PlanType fromString(String type) {
        if (StringUtils.isNotBlank(type)) {
            for (PlanType planType : values()) {
                if (planType.name().equalsIgnoreCase(type)) {
                    return planType;
                }
            }
        }
        return FREE;
    }

}
