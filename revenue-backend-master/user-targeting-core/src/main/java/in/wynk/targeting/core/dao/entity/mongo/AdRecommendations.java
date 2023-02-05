package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.targeting.core.constant.AdType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "ad_recommendations")
public class AdRecommendations implements Serializable {

    private static final long serialVersionUID = -946897553917169923L;

    @Id
    private String id;
    @Field("ad_type")
    private AdType type;
    @Field("ad_recommendations_list")
    private List<AdRecommendation> recommendations;

}
