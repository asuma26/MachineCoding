package in.wynk.partner.listing.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Abhishek
 * @created 09/09/20
 */
public interface ChannelPartnerLoggingMarker extends BaseLoggingMarkers {
    Marker ELIGIBLE_PLANS_API_ERROR = MarkerFactory.getMarker("ELIGIBLE_PLANS_API_ERROR");
    Marker ACTIVE_PLANS_API_ERROR = MarkerFactory.getMarker("ACTIVE_PLANS_API_ERROR");
}