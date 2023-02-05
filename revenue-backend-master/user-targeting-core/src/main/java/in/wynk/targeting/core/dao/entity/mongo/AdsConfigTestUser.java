package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.data.entity.MongoBaseEntity;
import in.wynk.targeting.core.dao.entity.mongo.music.UserConfig;
import in.wynk.targeting.core.dao.entity.mongo.persona.UserPersona;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@ToString
@SuperBuilder
@Document("adConfigTestUsers")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdsConfigTestUser extends MongoBaseEntity {

    private UserPersona userPersona;

    private UserConfig userconfig;

}
