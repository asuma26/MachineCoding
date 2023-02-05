package in.wynk.thanks.logging;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface ThanksLoggingMarkers extends BaseLoggingMarkers {

    Marker KINESIS_PUBLISH_ERROR = MarkerFactory.getMarker("KINESIS_PUBLISH_ERROR");
    Marker KINESIS_HANDLE_ERROR = MarkerFactory.getMarker("KINESIS_HANDLE_ERROR");
    Marker KINESIS_PUBLISH_SUCCESS = MarkerFactory.getMarker("KINESIS_PUBLISH_SUCCESS");
    Marker UUID_GENERATION_ERROR = MarkerFactory.getMarker("UUID_GENERATION_ERROR");
    Marker THANKS_ERROR = MarkerFactory.getMarker("THANKS_ERROR");
    Marker OLD_SEGMENT_ERROR = MarkerFactory.getMarker("OLD_SEGMENT_ERROR");
}
