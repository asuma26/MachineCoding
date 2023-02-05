package in.wynk.subscription.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@Document(collection = "ingress_intent_map")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngressIntentMap extends MongoBaseEntity {

    private String url;
    private String title;
    private String subtitle;

}
