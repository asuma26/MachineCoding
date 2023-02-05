package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.wynk.subscription.common.enums.PlanType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Document(collection = "plans")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Plan extends AbstractMongoSubscriptionBaseEntity implements Serializable, Comparable<Plan> {

    private int linkedOfferId;
    private Set<Integer> linkedOldProductIds;
    private Set<Integer> linkedOldFMFProductIds;
    private Set<Integer> linkedOldFreeProductIds;
    private String service;
    private Map<String, String> sku;
    private Price price;
    @lombok.Builder.Default
    private PlanType type = PlanType.ONE_TIME_SUBSCRIPTION;
    private Period period;
    private int hierarchy;
    private Map<String, Object> meta;
    private String title;
    private Set<String> eligiblePartners;
    private int linkedFreePlanId = -1;
    private String ruleExpression;

    @Override
    public boolean equals(Object o) {
        return ((Plan) o).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public int compareTo(Plan plan) {
        return this.getHierarchy() - plan.getHierarchy();
    }

}
