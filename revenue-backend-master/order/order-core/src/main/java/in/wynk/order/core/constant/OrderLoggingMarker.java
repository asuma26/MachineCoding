package in.wynk.order.core.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface OrderLoggingMarker extends BaseLoggingMarkers {

    Marker ORDER_ERROR = MarkerFactory.getMarker("ORDER_ERROR");

    Marker ORDER_PLACEMENT_INFO = MarkerFactory.getMarker("ORDER_PLACEMENT_ERROR");
    Marker ORDER_FULFILMENT_INFO = MarkerFactory.getMarker("ORDER_FULFILMENT_ERROR");
    Marker ORDER_NOTIFICATION_INFO = MarkerFactory.getMarker("ORDER_NOTIFICATION_ERROR");


    Marker ORDER_PLACEMENT_QUEUE = MarkerFactory.getMarker("ORDER_PLACEMENT_QUEUE");
    Marker ORDER_FULFILMENT_QUEUE = MarkerFactory.getMarker("ORDER_FULFILMENT_QUEUE");
    Marker ORDER_NOTIFICATION_QUEUE = MarkerFactory.getMarker("ORDER_NOTIFICATION_QUEUE");
    Marker DEFERRED_ORDER_QUEUE = MarkerFactory.getMarker("DEFERRED_ORDER_QUEUE");

    Marker ORDER_STATUS_ERROR = MarkerFactory.getMarker("ORDER_STATUS_ERROR");
    Marker ORDER_DEFERRED_ERROR = MarkerFactory.getMarker("ORDER_DEFERRED_ERROR");
    Marker ORDER_PLACEMENT_ERROR = MarkerFactory.getMarker("ORDER_PLACEMENT_ERROR");
    Marker ORDER_FULFILMENT_ERROR = MarkerFactory.getMarker("ORDER_FULFILMENT_ERROR");
    Marker ORDER_NOTIFICATION_ERROR = MarkerFactory.getMarker("ORDER_NOTIFICATION_ERROR");
}
