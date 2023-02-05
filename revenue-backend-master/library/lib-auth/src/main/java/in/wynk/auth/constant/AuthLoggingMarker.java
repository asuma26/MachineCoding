package in.wynk.auth.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface AuthLoggingMarker extends BaseLoggingMarkers {

    Marker BEAN_INITIALIZATION_ERROR = MarkerFactory.getMarker("BEAN_INITIALIZATION_ERROR");
    Marker INSUFFICIENT_REQUEST_HEADERS = MarkerFactory.getMarker("INSUFFICIENT_REQUEST_HEADERS");
    Marker AUTHENTICATION_FAILURE = MarkerFactory.getMarker("AUTHENTICATION_FAILURE");


}
