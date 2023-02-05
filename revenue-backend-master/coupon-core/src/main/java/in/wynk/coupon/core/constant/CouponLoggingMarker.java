package in.wynk.coupon.core.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface CouponLoggingMarker extends BaseLoggingMarkers {

    Marker APPLY_COUPON_ERROR = MarkerFactory.getMarker("APPLY_COUPON_ERROR");
    Marker REMOVE_COUPON_ERROR = MarkerFactory.getMarker("REMOVE_COUPON_ERROR");
    Marker EXHAUST_COUPON_ERROR = MarkerFactory.getMarker("EXHAUST_COUPON_ERROR");
    Marker COUPON_ELIGIBILITY_ERROR = MarkerFactory.getMarker("COUPON_ELIGIBILITY_ERROR");

}
