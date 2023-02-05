package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.targeting.core.constant.AdType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Map;

@Getter
@Builder
@ToString
@AllArgsConstructor
@Document(collection = "ad_properties")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdProperties implements Serializable {

    private static final long serialVersionUID = -946897553917169999L;
    @Id
    private String id;
    @Field("ad_type")
    private AdType type;
    @Field("ad_properties")
    private Map<String, Object> properties;
}
