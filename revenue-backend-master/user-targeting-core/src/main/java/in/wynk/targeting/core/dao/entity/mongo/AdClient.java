package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.data.entity.MongoBaseEntity;
import in.wynk.targeting.core.constant.AdSubType;
import in.wynk.targeting.core.constant.AdType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
@ToString
@Document(collection = "ad_clients")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdClient extends MongoBaseEntity implements Serializable {

    private static final long serialVersionUID = -946897553917169997L;
    @Field("wynk_client_id")
    private String clientId;
    @Field("wynk_client_supported_ad_types")
    private Map<AdType, List<AdSubType>> supportedType;
    @Setter
    @Field("wynk_client_enabled")
    private boolean enabled;

}
