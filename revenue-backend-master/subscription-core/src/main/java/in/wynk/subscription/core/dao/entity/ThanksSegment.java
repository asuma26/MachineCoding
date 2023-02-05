package in.wynk.subscription.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "thanks_segment")
public class ThanksSegment extends MongoBaseEntity {
    int hierarchy;
}
