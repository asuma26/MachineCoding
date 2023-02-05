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
public class BannerAdMeta extends AdMeta {

    @Field("ad_refresh_interval")
    @JsonProperty(value = "refreshInterval")
    private long refreshInterval;
    @Field("ad_templates")
    @JsonProperty(value = "adTemplates")
    private List<String> templates;
    @Field("ad_size")
    private String adSize;
    @Field("ad_unit_ids")
    private List<String> adUnitIds;
}
