package in.wynk.events.constants;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface EventsLoggingMarkers extends BaseLoggingMarkers {
    Marker UTILS_ERROR = MarkerFactory.getMarker("UTILS_ERROR");
}
