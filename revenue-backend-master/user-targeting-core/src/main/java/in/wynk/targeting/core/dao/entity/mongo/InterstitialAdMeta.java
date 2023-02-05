package in.wynk.targeting.core.dao.entity.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterstitialAdMeta extends AdMeta {
    @Field("ad_refresh_interval")
    @JsonProperty(value = "refreshInterval")
    private long refresh;
    @Field("ad_templates")
    @JsonProperty(value = "adTemplates")
    private List<String> templates;
    @Field("triggers")
    @JsonProperty(value = "triggers")
    private List<Long> triggers;
    @Field("ad_unit_ids")
    private List<String> adUnitIds;
}
