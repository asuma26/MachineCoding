package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.wynk.subscription.core.dao.entity.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MongoDbCollections {
    List<Offer> offers;
    List<OfferMap> offerMaps;
    List<Partner> partners;
    List<Product> products;
    List<Plan> plans;
}
