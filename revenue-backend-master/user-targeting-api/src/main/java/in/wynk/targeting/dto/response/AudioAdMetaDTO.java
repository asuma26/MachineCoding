package in.wynk.targeting.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.wynk.targeting.core.dao.entity.mongo.AudioAdMeta;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class AudioAdMetaDTO {

    @Field("ad_frequency_interval")
    @JsonProperty(value = "freqInterval")
    private final long frequency;
    @Field("ad_black_listed_cps")
    @JsonProperty(value = "blackListedCps")
    @Setter
    private Set<String> blacklistedCps;
    @Field("ad_templates")
    @JsonProperty(value = "adTemplates")
    private final List<String> templates;
    @Field("ad_unit_ids")
    private final List<String> adUnitIds;
    private final String source;

    public AudioAdMetaDTO(AudioAdMeta audioAdMeta) {
        this.frequency = audioAdMeta.getFrequency();
        if (CollectionUtils.isNotEmpty(audioAdMeta.getBlacklistedCps())) {
            this.blacklistedCps = new HashSet<>(audioAdMeta.getBlacklistedCps());
        } else {
            this.blacklistedCps = new HashSet<>();
        }
        this.templates = audioAdMeta.getTemplates();
        this.adUnitIds = audioAdMeta.getAdUnitIds();
        this.source = audioAdMeta.getSource();
    }

}
