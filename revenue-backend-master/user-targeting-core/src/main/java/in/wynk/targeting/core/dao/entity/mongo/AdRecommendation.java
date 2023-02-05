package in.wynk.targeting.core.dao.entity.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdRecommendation {

    @Field("ad_recommendation_hierarchy")
    private int hierarchy;
    @Field("ad_id")
    @JsonProperty(value = "adId")
    private String slotId;
    @Field("recommendation_type")
    private String type;
    @Field("is_recommended")
    private boolean isRecommended;

}
