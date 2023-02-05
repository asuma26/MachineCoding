package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@SuperBuilder
@AllArgsConstructor
@Document(collection = "wynk_client")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WynkClient extends MongoBaseEntity {

    private String os;
    private String service;
    @Field("client_id")
    private String clientId;
    @Field("external_partner")
    private boolean externalPartner;

}
