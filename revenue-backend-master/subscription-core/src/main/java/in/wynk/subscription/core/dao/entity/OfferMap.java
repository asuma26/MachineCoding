package in.wynk.subscription.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@ToString
@SuperBuilder
@Document(collection = "offer_map")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferMap extends MongoBaseEntity {

    private Map<Integer, OfferPlanMapping> offerIds;

}
