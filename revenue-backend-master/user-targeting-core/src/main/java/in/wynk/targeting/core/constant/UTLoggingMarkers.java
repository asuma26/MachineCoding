package in.wynk.targeting.core.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface UTLoggingMarkers extends BaseLoggingMarkers {

    Marker MUSIC_CONFIG_ERROR = MarkerFactory.getMarker("MUSIC_CONFIG_ERROR");
    Marker PERSONA_ERROR = MarkerFactory.getMarker("PERSONA_ERROR");
    Marker UTILS_ERROR = MarkerFactory.getMarker("UTILS_ERROR");
    Marker USER_PROFILE_ERROR = MarkerFactory.getMarker("USER_PROFILE_ERROR");
}
