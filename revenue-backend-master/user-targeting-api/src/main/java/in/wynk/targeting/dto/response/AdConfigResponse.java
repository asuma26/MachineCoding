package in.wynk.targeting.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import in.wynk.targeting.core.dao.entity.mongo.BannerAdMeta;
import in.wynk.targeting.core.dao.entity.mongo.InterstitialAdMeta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.wynk.targeting.core.constant.AdConstants.SLOT_CONFIGS;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AnalysedEntity
public class AdConfigResponse implements Serializable {

    private static final long serialVersionUID = 4454038461769174428L;

    @Analysed
    private boolean enabled;

    private Map<String, Object> bannerAds;
    private Map<String, Object> interstitialAds;
    private Map<String, Object> videoAds;
    private Map<String, Object> audioAds;
    private Map<String, String> dfpParams;

    private static final AdConfigResponse DISABLED_RESPONSE = AdConfigResponse.builder(false).build();

    public static AdConfigResponse defaultDisabledResponse() {
        return DISABLED_RESPONSE;
    }

    public static AdConfigResponseBuilder builder(boolean enabled) {
        return new AdConfigResponseBuilder(enabled);
    }

    @Getter
    public static final class AdConfigResponseBuilder {
        private final boolean enabled;
        private Map<String, Object> bannerAds;
        private Map<String, Object> interstitialAds;
        private Map<String, Object> videoAds;
        private Map<String, Object> audioAds;
        private Map<String, String> dfpParams;

        private AdConfigResponseBuilder(boolean enabled) {
            this.enabled = enabled;
        }

        public AdConfigResponseBuilder bannerAds(Map<String, Object> bannerAds) {
            this.bannerAds = bannerAds;
            return this;
        }

        public AdConfigResponseBuilder interstitialAds(Map<String, Object> interstitialAds) {
            this.interstitialAds = interstitialAds;
            return this;
        }

        public AdConfigResponseBuilder videoAds(Map<String, Object> videoAds) {
            this.videoAds = videoAds;
            return this;
        }

        public AdConfigResponseBuilder audioAds(Map<String, Object> audioAds) {
            this.audioAds = audioAds;
            return this;
        }

        private void initializeDfpParams() {
            if (MapUtils.isEmpty(dfpParams)) {
                dfpParams = new HashMap<>();
            }
        }

        private void initializeBannerAds() {
            if (MapUtils.isEmpty(bannerAds)) {
                bannerAds = new HashMap<>();
                bannerAds.put(SLOT_CONFIGS, new HashMap<>());
            }
        }

        private <V> void putInBannerAds(String key, V item) {
            bannerAds.put(key, item);
        }

        private <V> V getFromBannerAds(String key) {
            return (V) bannerAds.get(key);
        }

        private <V> V getFromInterstitialAds(String key) {
            return (V) interstitialAds.get(key);
        }

        private void initializeInterstitialAds() {
            if (MapUtils.isEmpty(interstitialAds)) {
                interstitialAds = new HashMap<>();
                interstitialAds.put(SLOT_CONFIGS, new HashMap<>());
            }
        }

        private void initializeVideoAds() {
            if (MapUtils.isEmpty(videoAds)) {
                videoAds = new HashMap<>();
            }
        }

        private void initializeAudioAds() {
            if (MapUtils.isEmpty(audioAds)) {
                audioAds = new HashMap<>();
            }
        }

        public void dfpParams(Map<String, String> dfpParams) {
            initializeDfpParams();
            this.dfpParams.putAll(dfpParams);
        }

        public AdConfigResponse build() {
            AdConfigResponse adConfigResponse = new AdConfigResponse();
            adConfigResponse.dfpParams = dfpParams;
            adConfigResponse.enabled = this.enabled;
            adConfigResponse.audioAds = this.audioAds;
            adConfigResponse.bannerAds = this.bannerAds;
            adConfigResponse.videoAds = this.videoAds;
            adConfigResponse.interstitialAds = this.interstitialAds;
            return adConfigResponse;
        }

        public void addDfpParam(String key, String value) {
            initializeDfpParams();
            this.dfpParams.put(key, value);
        }

        public void addBannerSlotConfig(String slotId, BannerAdMeta meta) {
            initializeBannerAds();
            Map<String, BannerAdMeta> adMeta = getFromBannerAds(SLOT_CONFIGS);
            adMeta.put(slotId, meta);
        }

        public void addInterstitialSlotConfig(String slotId, InterstitialAdMeta meta) {
            initializeInterstitialAds();
            Map<String, InterstitialAdMeta> adMeta = getFromInterstitialAds(SLOT_CONFIGS);
            adMeta.put(slotId, meta);
        }

        public void addInterstitialProperties(Map<String, Object> properties) {
            initializeInterstitialAds();
            interstitialAds.putAll(properties);
        }

        public void addAudioProperties(Map<String, Object> properties) {
            initializeAudioAds();
            audioAds.putAll(properties);
        }

        public void addVideoProperties(Map<String, Object> properties) {
            initializeVideoAds();
            videoAds.putAll(properties);
        }

        public void addAudioAd(AdConfig adConfig) {
            initializeAudioAds();
            Map<String, SingleAudioAdMetaDTO> temp = (Map<String, SingleAudioAdMetaDTO>) audioAds.getOrDefault(SLOT_CONFIGS, new HashMap<>());
            SingleAudioAdMetaDTO audioAdConfig = temp.getOrDefault(adConfig.getSubType().toString(), new SingleAudioAdMetaDTO());
            audioAdConfig.getAudioSlots().put(adConfig.getSlotId(), new AudioAdMetaDTO(adConfig.getMeta()));
            temp.put(adConfig.getSubType().toString(), audioAdConfig);
            audioAds.put(SLOT_CONFIGS, temp);
        }

        public void addVideoAd(AdConfig adConfig) {
            initializeVideoAds();
            Map<String, SingleVideoAdMetaDTO> temp = (Map<String, SingleVideoAdMetaDTO>) videoAds.getOrDefault(SLOT_CONFIGS, new HashMap<>());
            SingleVideoAdMetaDTO videoAdConfig = temp.getOrDefault(adConfig.getSubType().toString(), new SingleVideoAdMetaDTO());
            videoAdConfig.getVideoSlots().put(adConfig.getSlotId(), new VideoAdMetaDTO(adConfig.getMeta()));
            temp.put(adConfig.getSubType().toString(), videoAdConfig);
            videoAds.put(SLOT_CONFIGS, temp);
        }

        public void populateAdMeta(AdConfig adConfig) {
            switch (adConfig.getType()) {
                case BANNER_AD:
                    addBannerSlotConfig(adConfig.getSlotId(), adConfig.getMeta());
                    break;
                case INTERSTITIAL_AD:
                    addInterstitialSlotConfig(adConfig.getSlotId(), adConfig.getMeta());
                    break;
                case AUDIO_AD:
                    addAudioAd(adConfig);
                    break;
                case VIDEO_AD:
                    addVideoAd(adConfig);
                    break;
            }
        }

        public void populateAdMetaV3(AdConfig adConfig) {
            switch (adConfig.getType()) {
                case BANNER_AD:
                    addBannerSlotConfigV3(adConfig);
                    break;
                case INTERSTITIAL_AD:
                    addInterstitialSlotConfigV3(adConfig);
                    break;
                case AUDIO_AD:
                    addAudioAdV3(adConfig);
                    break;
                case VIDEO_AD:
                    addVideoAdV3(adConfig);
                    break;
            }
        }

        private void addBannerSlotConfigV3(AdConfig adConfig) {
            initializeBannerAds();
            Map<String, List<BannerAdMeta>> adMeta = getFromBannerAds(SLOT_CONFIGS);
            List<BannerAdMeta> bannerAdMetas = adMeta.getOrDefault(adConfig.getSlotId(), new ArrayList<>());
            bannerAdMetas.add(adConfig.getMeta());
            adMeta.put(adConfig.getSlotId(), bannerAdMetas);
        }

        private void addInterstitialSlotConfigV3(AdConfig adConfig) {
            initializeInterstitialAds();
            Map<String, List<InterstitialAdMeta>> adMeta = getFromInterstitialAds(SLOT_CONFIGS);
            List<InterstitialAdMeta> interstitialAdMetas = adMeta.getOrDefault(adConfig.getSlotId(), new ArrayList<>());
            interstitialAdMetas.add(adConfig.getMeta());
            adMeta.put(adConfig.getSlotId(), interstitialAdMetas);
        }

        private void addAudioAdV3(AdConfig adConfig) {
            initializeAudioAds();
            Map<String, MultiAudioAdMetaDTO> temp = (Map<String, MultiAudioAdMetaDTO>) audioAds.getOrDefault(SLOT_CONFIGS, new HashMap<>());
            MultiAudioAdMetaDTO audioAdConfig = temp.getOrDefault(adConfig.getSubType().toString(), new MultiAudioAdMetaDTO());
            List<AudioAdMetaDTO> audioAdMetaDTOS = audioAdConfig.getAudioSlots().getOrDefault(adConfig.getSlotId(), new ArrayList<>());
            audioAdMetaDTOS.add(new AudioAdMetaDTO(adConfig.getMeta()));
            audioAdConfig.getAudioSlots().put(adConfig.getSlotId(), audioAdMetaDTOS);
            temp.put(adConfig.getSubType().toString(), audioAdConfig);
            audioAds.put(SLOT_CONFIGS, temp);
        }

        private void addVideoAdV3(AdConfig adConfig) {
            initializeVideoAds();
            Map<String, MultiVideoAdMetaDTO> temp = (Map<String, MultiVideoAdMetaDTO>) videoAds.getOrDefault(SLOT_CONFIGS, new HashMap<>());
            MultiVideoAdMetaDTO videoAdConfig = temp.getOrDefault(adConfig.getSubType().toString(), new MultiVideoAdMetaDTO());
            List<VideoAdMetaDTO> videoAdMetaDTOS = videoAdConfig.getVideoSlots().getOrDefault(adConfig.getSlotId(), new ArrayList<>());
            videoAdMetaDTOS.add(new VideoAdMetaDTO(adConfig.getMeta()));
            videoAdConfig.getVideoSlots().put(adConfig.getSlotId(), videoAdMetaDTOS);
            temp.put(adConfig.getSubType().toString(), videoAdConfig);
            videoAds.put(SLOT_CONFIGS, temp);
        }
    }

}