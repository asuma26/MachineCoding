package in.wynk.targeting.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum AdType implements Serializable {

    BANNER_AD("BANNER_AD"), VIDEO_AD("VIDEO_AD"), AUDIO_AD("AUDIO_AD"), INTERSTITIAL_AD("INTERSTITIAL_AD");

    private final String type;

    @Override
    public String toString() {
        return type;
    }

}
