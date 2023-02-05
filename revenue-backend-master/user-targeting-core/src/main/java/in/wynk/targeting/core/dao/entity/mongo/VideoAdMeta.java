package in.wynk.targeting.core.dao.entity.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoAdMeta extends AdMeta {
    @Field("ad_unit_id")
    @JsonProperty(value = "adUnitId")
    private String unitId;
    @Field("ad_frequency_interval")
    @JsonProperty(value = "freqInterval")
    private long frequency;
    @Field("ad_black_listed_cps")
    @JsonProperty(value = "blackListedCps")
    @Setter
    private List<String> blacklistedCps;
    @Field("companion_banner_width")
    private Integer companionBannerWidth;
    @Field("companion_banner_height")
    private Integer companionBannerHeight;
    @Field("targeted")
    private boolean targeted;
}
