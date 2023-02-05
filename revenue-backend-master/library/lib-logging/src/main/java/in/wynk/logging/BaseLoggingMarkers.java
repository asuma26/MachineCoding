package in.wynk.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface BaseLoggingMarkers {

    Marker APPLICATION_ERROR = MarkerFactory.getMarker("APPLICATION_ERROR");

    Marker SPRING_REQUEST_ERROR = MarkerFactory.getMarker("SPRING_REQUEST_ERROR");

    Marker APPLICATION_INVALID_USECASE = MarkerFactory.getMarker("APPLICATION_INVALID_USECASE");

    Marker INVALID_VALUE = MarkerFactory.getMarker("INVALID_VALUE");

    Marker HTTP_ERROR = MarkerFactory.getMarker("HTTP_ERROR");

    Marker SERVICE_PARTNER_ERROR = MarkerFactory.getMarker("SERVICE_PARTNER_ERROR");

    Marker CASSANDRA_ERROR = MarkerFactory.getMarker("CASSANDRA_ERROR");

    Marker REDIS_ERROR = MarkerFactory.getMarker("REDIS_ERROR");

    Marker CACHE_ERROR = MarkerFactory.getMarker("CACHE_ERROR");

    Marker CACHE_READ_ERROR = MarkerFactory.getMarker("CACHE_READ_ERROR");

    Marker CACHE_WRITE_ERROR = MarkerFactory.getMarker("CACHE_WRITE_ERROR");

    Marker MONGO_ERROR = MarkerFactory.getMarker("MONGO_ERROR");

    Marker JMS_ERROR = MarkerFactory.getMarker("JMS_ERROR");

    Marker PRE_CONDITION_FAILURE = MarkerFactory.getMarker("PRE_CONDITION_FAILURE");

    Marker VAS_ERROR = MarkerFactory.getMarker("VAS_ERROR");

    Marker PAYMENT_ERROR = MarkerFactory.getMarker("PAYMENT_ERROR");

    Marker ENCRYPTION_ERROR = MarkerFactory.getMarker("ENCRYPTION_ERROR");
    Marker DECRYPTION_ERROR = MarkerFactory.getMarker("DECRYPTION_ERROR");

    Marker WYNK_HYSTRIX_ERROR = MarkerFactory.getDetachedMarker("WYNK_HYSTRIX_ERROR");

    Marker WYNK_HYSTRIX = MarkerFactory.getDetachedMarker("WYNK_HYSTRIX");

    Marker EXTERNAL_CLIENT_RESPONSE = MarkerFactory.getMarker("EXTERNAL_CLIENT_RESPONSE");

    Marker EXTERNAL_SERVICE_FAILURE = MarkerFactory.getMarker("EXTERNAL_SERVICE_FAILURE");
}
