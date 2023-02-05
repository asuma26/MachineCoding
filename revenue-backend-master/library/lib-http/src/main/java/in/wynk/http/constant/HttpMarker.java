package in.wynk.http.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface HttpMarker extends BaseLoggingMarkers {

    Marker REST_TEMPLATE_REGISTRATION = MarkerFactory.getMarker("REST_TEMPLATE_REGISTRATION");

}
