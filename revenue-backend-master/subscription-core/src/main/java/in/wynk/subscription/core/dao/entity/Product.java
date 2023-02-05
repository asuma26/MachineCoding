package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends AbstractMongoSubscriptionBaseEntity implements Serializable, Comparable<Product> {

    private String cpProductId;
    private String service;
    private String packGroup;
    private int hierarchy;
    private String title;
    private String cpName;
    @Deprecated
    private List<String> eligibleAppIds;

    private Map<String,Integer> appIdHierarchy;

    private Map<String, Object> features;

    @Override
    public boolean equals(Object o) {
        return ((Plan)o).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }


    @Override
    public int compareTo(Product product) {
        return this.hierarchy - product.hierarchy;
    }
}