package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.wynk.subscription.common.enums.ProvisionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Document(collection = "offers")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Offer extends AbstractMongoSubscriptionBaseEntity implements Serializable, Comparable<Offer> {

    private String description;
    private int hierarchy;
    private int displayOrder = Integer.MAX_VALUE;
    private Messages messages;
    private Map<String, String> products;
    private String packGroup;
    private List<Integer> plans;
    private ProvisionType provisionType;
    private String ruleExpression;
    private String service;
    private String title;
    private String button;
    private List<DescriptionObject> descriptions;
    private String comboHeader;
    private String colorCode;
    private String subtitle;
    private Map<String, Object> meta;
    private boolean isCombo;
    @JsonIgnore
    public int getFreePlan() {
        return plans.get(0);
    }

    @JsonIgnore
    public boolean isPaid() {
        return provisionType == ProvisionType.PAID;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return ((Offer) obj).getId() == this.getId();
    }

    @Override
    public int compareTo(Offer offer) {
        return this.hierarchy - offer.getHierarchy();
    }

    @Override
    public String toString() {
        return "Offer{" +
                ", id=" + getId() +
                ", hierarchy=" + hierarchy +
                ", ruleExpression='" + ruleExpression + '\'' +
                ", provisionType=" + provisionType +
                ", products=" + products +
                ", packGroup='" + packGroup + '\'' +
                ", plans=" + plans +
                ", service='" + service + '\'' +
                ", title='" + title +
                '}';
    }
}
