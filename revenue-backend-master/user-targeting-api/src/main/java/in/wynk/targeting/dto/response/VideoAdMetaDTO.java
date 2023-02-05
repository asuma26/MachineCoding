package in.wynk.targeting.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.wynk.targeting.core.dao.entity.mongo.VideoAdMeta;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Getter
public class VideoAdMetaDTO {

    @JsonProperty(value = "adUnitId")
    private final String unitId;
    @JsonProperty(value = "freqInterval")
    private final long frequency;
    @Setter
    private Set<String> blackListedCps;
    private final Integer companionBannerWidth;
    private final Integer companionBannerHeight;
    private final boolean targeted;
    private final String source;

    public VideoAdMetaDTO(VideoAdMeta videoAdMeta) {
        this.unitId = videoAdMeta.getUnitId();
        if(CollectionUtils.isNotEmpty(videoAdMeta.getBlacklistedCps())){
            this.blackListedCps = new HashSet<>(videoAdMeta.getBlacklistedCps());
        } else {
            this.blackListedCps = new HashSet<>();
        }
        this.frequency = videoAdMeta.getFrequency();
        this.companionBannerHeight = videoAdMeta.getCompanionBannerHeight();
        this.companionBannerWidth = videoAdMeta.getCompanionBannerWidth();
        this.targeted = videoAdMeta.isTargeted();
        this.source = videoAdMeta.getSource();
    }

}
