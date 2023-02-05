package in.wynk.client.core.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface ClientLoggingMarker extends BaseLoggingMarkers {

    Marker CLIENT_DETAILS_CATCHING_FAILURE = MarkerFactory.getMarker("CLIENT_DETAILS_CATCHING_FAILURE");
    Marker INVALID_CLIENT_DETAILS = MarkerFactory.getMarker("INVALID_CLIENT_DETAILS");
    Marker CLIENT_COMMUNICATION_ERROR = MarkerFactory.getMarker("CLIENT_COMMUNICATION_ERROR");

}
