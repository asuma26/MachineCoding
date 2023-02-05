package in.wynk.common.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface CommonLoggingMarker extends BaseLoggingMarkers {

    Marker CHECKSUM_GENERATION_FAILURE = MarkerFactory.getMarker("CHECKSUM_GENERATION_FAILURE");

}
