package in.wynk.queue.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface QueueLoggingMarker extends BaseLoggingMarkers {

    Marker SQS_ERROR = MarkerFactory.getMarker("SQS_ERROR");

    Marker MESSAGE_PUBLISH_ERROR = MarkerFactory.getMarker("MESSAGE_PUBLISH_ERROR");

}
